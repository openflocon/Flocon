package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_success
import flocondesktop.composeapp.generated.resources.settings_test_failure
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.SetFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.common.log.LogManager
import io.github.openflocon.flocondesktop.common.log.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.flocondesktop.features.onboarding.OnboardingRoutes
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import org.jetbrains.compose.resources.getString

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    fontSizeMultiplierUseCase: ObserveFontSizeMultiplierUseCase,
    private val setFontSizeMultiplierUseCase: SetFontSizeMultiplierUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
    private val logManager: LogManager,
    private val navigationState: MainFloconNavigationState,
    private val startAdbForwardUseCase: StartAdbForwardUseCase,
    private val messagesServerDelegate: MessagesServerDelegate,
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    val uiState = combine(
        fontSizeMultiplierUseCase(),
        observeThemeUseCase(),
        logManager.logs,
        settingsRepository.adbForwardStatus,
        messagesServerDelegate.serverError,
    ) { multiplier, theme, logs, forwardStatus, serverErrorMsg ->
        SettingsUiState(
            fontSizeMultiplier = multiplier,
            theme = theme,
            logs = logs.map { it.toUiModel() },
            adbForwardStatus = forwardStatus,
            serverError = serverErrorMsg,
        )
    val uiState = combine(fontSizeMultiplierUseCase(), logManager.logs) { multiplier, logs ->
        SettingsUiState(
            fontSizeMultiplier = multiplier,
            logs = logs.map { it.toUiModel() },
            adbForwardStatus = forwardStatus,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                fontSizeMultiplier = 1f,
                theme = ThemeSetting.DEFAULT,
                logs = emptyList(),
                adbForwardStatus = AdbForwardStatus.UNKNOWN,
                serverError = null
            )
        )

    init {
        viewModelScope.launch {
            settingsRepository.adbPath.collect { path ->
                path?.let {
                    _adbPathInput.value = it
                    testAdbUseCase(it).fold(
                        doOnFailure = {
                            initialSetupStateHolder.setRequiresInitialSetup()
                        },
                        doOnSuccess = {
                            initialSetupStateHolder.setAdbIsWorking()
                        }
                    )
                }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.FontSizeMultiplierChange -> onFontSizeMultiplierChange(action)
            is SettingsAction.ThemeChange -> onThemeChange(action)
        }
    }

    private fun onFontSizeMultiplierChange(action: SettingsAction.FontSizeMultiplierChange) {
        viewModelScope.launch {
            setFontSizeMultiplierUseCase(action.value)
        }
    }

    private fun onThemeChange(action: SettingsAction.ThemeChange) {
        viewModelScope.launch {
            setThemeUseCase(action.value)
        }
    }

    fun onAdbPathChanged(newPath: String) {
        _adbPathInput.value = newPath
    }

    fun saveAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val path = adbPathInput.value
            testAdbUseCase(path).fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        message = "Cannot save: ADB path is invalid.",
                        type = FeedbackDisplayer.MessageType.Error
                    )
                },
                doOnSuccess = {
                    saveAdb()
                    feedbackDisplayer.displayMessage("ADB path saved successfully!")
                }
            )
        }
    }

    private suspend fun saveAdb() {
        val path = adbPathInput.value
        Logger.d(TAG) { "Saving ADB path: $path" }
        settingsRepository.setAdbPath(path)
        logManager.d(TAG, "Saving ADB path: $path")
    }

    fun testAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val path = adbPathInput.value
            Logger.d(TAG) { "Testing ADB path: $path" }
            logManager.d(TAG, "Testing ADB path: $path")
            testAdbUseCase(path).fold(
                doOnFailure = {
                    val msg = "ADB test failed: ${it.message}"
                    Logger.e(TAG, it) { msg }
                    logManager.e(TAG, "ADB test failed", it)
                    feedbackDisplayer.displayMessage(
                        message = getString(Res.string.settings_test_failure, it.localizedMessage),
                        type = FeedbackDisplayer.MessageType.Error
                    )
                    initialSetupStateHolder.setRequiresInitialSetup()
                },
                doOnSuccess = {
                    Logger.d(TAG) { "ADB test succeeded" }
                    logManager.d(TAG, "ADB test succeeded")
                    feedbackDisplayer.displayMessage(getString(Res.string.general_success))
                    initialSetupStateHolder.setAdbIsWorking()
                    saveAdb()
                },
            )
        }
    }

    fun launchOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(false)
            navigationState.navigate(OnboardingRoutes.Main)
        }
    }

    fun clearLogs() {
        logManager.clear()
    }

    fun relaunchAdbAndServer() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            logManager.d(TAG, "User requested relaunch of ADB Server connection & websocket server")
            messagesServerDelegate.relaunchServer()
            startAdbForwardUseCase().fold(
                doOnSuccess = {
                    settingsRepository.setAdbForwardStatus(AdbForwardStatus.OK)
                    feedbackDisplayer.displayMessage("Services relaunched successfully")
                },
                doOnFailure = {
                    settingsRepository.setAdbForwardStatus(AdbForwardStatus.NOK)
                    feedbackDisplayer.displayMessage(
                        message = "ADB Port Forward failed: ${it.message}",
                        type = FeedbackDisplayer.MessageType.Error
                    )
                }
            )
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

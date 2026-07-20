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
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    val uiState = combine(
        fontSizeMultiplierUseCase(),
        observeThemeUseCase(),
        logManager.logs,
        settingsRepository.adbForwardStatus,
    ) { multiplier, theme, logs, forwardStatus ->
        SettingsUiState(
            fontSizeMultiplier = multiplier,
            theme = theme,
            logs = logs.map { it.toUiModel() },
            adbForwardStatus = forwardStatus,
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
                adbForwardStatus = AdbForwardStatus.UNKNOWN
            )
        )

    init {
        viewModelScope.launch {
            // Utiliser GlobalScope ici pour la simplicité de l'exemple, mais préférez un scope dédié
            settingsRepository.adbPath.collect { path ->
                path?.let { _adbPathInput.value = it }
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
            saveAdb()
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
            saveAdb()
            val path = adbPathInput.value
            Logger.d(TAG) { "Testing ADB path: $path" }
            logManager.d(TAG, "Testing ADB path: $path")
            testAdbUseCase().fold(
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
                },
            )
        }
    }

    fun clearLogs() {
        logManager.clear()
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}

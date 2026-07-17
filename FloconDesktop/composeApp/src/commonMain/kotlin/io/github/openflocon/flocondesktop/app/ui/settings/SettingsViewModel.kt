package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_success
import flocondesktop.composeapp.generated.resources.settings_test_failure
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.ObserveThemeUseCase
import io.github.openflocon.domain.settings.usecase.SetFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.SetThemeUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
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
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    val uiState = combine(
        fontSizeMultiplierUseCase(),
        observeThemeUseCase(),
    ) { fontSizeMultiplier, theme ->
        SettingsUiState(
            fontSizeMultiplier = fontSizeMultiplier,
            theme = theme,
        )
    }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                fontSizeMultiplier = 1f,
                theme = ThemeSetting.DEFAULT,
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
        settingsRepository.setAdbPath(adbPathInput.value)
    }

    fun testAdbPath() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveAdb()
            testAdbUseCase().fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        message = getString(Res.string.settings_test_failure, it.localizedMessage),
                        type = FeedbackDisplayer.MessageType.Error
                    )
                    initialSetupStateHolder.setRequiresInitialSetup()
                },
                doOnSuccess = {
                    feedbackDisplayer.displayMessage(getString(Res.string.general_success))
                    initialSetupStateHolder.setAdbIsWorking()
                },
            )
        }
    }
}

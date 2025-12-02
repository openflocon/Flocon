package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.SetFontSizeMultiplierUseCase
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    fontSizeMultiplierUseCase: ObserveFontSizeMultiplierUseCase,
    private val setFontSizeMultiplierUseCase: SetFontSizeMultiplierUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    val uiState = fontSizeMultiplierUseCase().map {
        SettingsUiState(fontSizeMultiplier = it)
    }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(fontSizeMultiplier = 1f)
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
        }
    }

    private fun onFontSizeMultiplierChange(action: SettingsAction.FontSizeMultiplierChange) {
        viewModelScope.launch {
            setFontSizeMultiplierUseCase(action.value)
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
                    feedbackDisplayer.displayMessage("failed")
                    initialSetupStateHolder.setRequiresInitialSetup()
                },
                doOnSuccess = {
                    feedbackDisplayer.displayMessage("success")
                    initialSetupStateHolder.setAdbIsWorking()
                },
            )
        }
    }
}

package io.github.openflocon.flocondesktop.main.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    private val _displayAboutScreen = MutableStateFlow(false)
    val displayAboutScreen = _displayAboutScreen.asStateFlow()
    val needsAdbSetup = initialSetupStateHolder.needsAdbSetup

    init {
        viewModelScope.launch {
            // Utiliser GlobalScope ici pour la simplicité de l'exemple, mais préférez un scope dédié
            settingsRepository.adbPath.collect { path ->
                path?.let { _adbPathInput.value = it }
            }
        }
    }

    fun displayAboutScreen() {
        _displayAboutScreen.value = true
    }

    fun hideAboutScreen() {
        _displayAboutScreen.value = false
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

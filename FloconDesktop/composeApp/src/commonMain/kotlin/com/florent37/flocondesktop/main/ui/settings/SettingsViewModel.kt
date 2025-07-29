package com.florent37.flocondesktop.main.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayer
import com.florent37.flocondesktop.core.domain.settings.TestAdbUseCase
import com.florent37.flocondesktop.core.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {
    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput = _adbPathInput.asStateFlow()
    private val _displayAboutScreen = MutableStateFlow(false)
    val displayAboutScreen = _displayAboutScreen.asStateFlow()

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
        settingsRepository.setAdbPath(adbPathInput.value)
    }

    fun testAdbPath() {
        testAdbUseCase().fold(
            doOnFailure = {
                feedbackDisplayer.displayMessage("failed : ${it.message}")
            },
            doOnSuccess = {
                feedbackDisplayer.displayMessage("success")
            },
        )
    }
}

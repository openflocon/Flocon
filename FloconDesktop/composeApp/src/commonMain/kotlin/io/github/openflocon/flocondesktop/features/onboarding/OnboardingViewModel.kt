package io.github.openflocon.flocondesktop.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.TestAdbUseCase
import io.github.openflocon.flocondesktop.app.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class OnboardingStep {
    AdbConfig,
    FloconSetup,
    LaunchApp,
    Success
}

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
    private val testAdbUseCase: TestAdbUseCase,
    private val devicesDelegate: DevicesDelegate,
    private val navigationState: MainFloconNavigationState,
) : ViewModel() {

    private val _currentStep = MutableStateFlow(OnboardingStep.AdbConfig)
    val currentStep: StateFlow<OnboardingStep> = _currentStep.asStateFlow()

    private val _adbPathInput = MutableStateFlow("")
    val adbPathInput: StateFlow<String> = _adbPathInput.asStateFlow()

    private val _adbValid = MutableStateFlow<Boolean?>(null)
    val adbValid: StateFlow<Boolean?> = _adbValid.asStateFlow()

    private val _isAdbTesting = MutableStateFlow(false)
    val isAdbTesting: StateFlow<Boolean> = _isAdbTesting.asStateFlow()

    private val _appDetected = MutableStateFlow(false)
    val appDetected: StateFlow<Boolean> = _appDetected.asStateFlow()

    private val _successCountdown = MutableStateFlow(5)
    val successCountdown: StateFlow<Int> = _successCountdown.asStateFlow()

    init {
        viewModelScope.launch {
            val path = settingsRepository.getAdbPath() ?: ""
            _adbPathInput.value = path
            if (path.isNotEmpty()) {
                _isAdbTesting.value = true
                testAdbUseCase(path).fold(
                    doOnFailure = { _adbValid.value = false },
                    doOnSuccess = { _adbValid.value = true }
                )
                _isAdbTesting.value = false
            }
        }

        viewModelScope.launch {
            devicesDelegate.appsState.collect { state ->
                val detected = state is AppsStateUiModel.WithApps && state.apps.isNotEmpty()
                _appDetected.value = detected
                if (detected && _currentStep.value == OnboardingStep.LaunchApp) {
                    goToStep(OnboardingStep.Success)
                }
            }
        }
    }

    fun onAdbPathChanged(newPath: String) {
        _adbPathInput.value = newPath
        _adbValid.value = null // reset validation state on edit
    }

    fun testAdbPath() {
        val path = adbPathInput.value
        viewModelScope.launch {
            _isAdbTesting.value = true
            testAdbUseCase(path).fold(
                doOnFailure = {
                    _adbValid.value = false
                },
                doOnSuccess = {
                    _adbValid.value = true
                    settingsRepository.setAdbPath(path)
                }
            )
            _isAdbTesting.value = false
        }
    }

    fun goToStep(step: OnboardingStep) {
        _currentStep.value = step
        if (step == OnboardingStep.Success) {
            startSuccessCountdown()
        }
    }

    fun nextStep() {
        val next = when (_currentStep.value) {
            OnboardingStep.AdbConfig -> OnboardingStep.FloconSetup
            OnboardingStep.FloconSetup -> OnboardingStep.LaunchApp
            OnboardingStep.LaunchApp -> OnboardingStep.Success
            OnboardingStep.Success -> OnboardingStep.Success
        }
        goToStep(next)
    }

    fun previousStep() {
        val prev = when (_currentStep.value) {
            OnboardingStep.AdbConfig -> OnboardingStep.AdbConfig
            OnboardingStep.FloconSetup -> OnboardingStep.AdbConfig
            OnboardingStep.LaunchApp -> OnboardingStep.FloconSetup
            OnboardingStep.Success -> OnboardingStep.LaunchApp
        }
        goToStep(prev)
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
            navigationState.menu(NetworkRoutes.Main)
        }
    }

    private fun startSuccessCountdown() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
            for (i in 5 downTo 1) {
                _successCountdown.value = i
                delay(1000)
            }
            navigationState.menu(NetworkRoutes.Main)
        }
    }
}

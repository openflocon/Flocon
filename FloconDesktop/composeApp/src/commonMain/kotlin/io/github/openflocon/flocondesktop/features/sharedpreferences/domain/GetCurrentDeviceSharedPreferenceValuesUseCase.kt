package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class GetCurrentDeviceSharedPreferenceValuesUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke() {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        val sharedPreferenceId = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return
        sharedPreferenceRepository.getDeviceSharedPreferencesValues(
            deviceId = currentDevice,
            sharedPreferenceId = sharedPreferenceId.id,
        )
    }
}

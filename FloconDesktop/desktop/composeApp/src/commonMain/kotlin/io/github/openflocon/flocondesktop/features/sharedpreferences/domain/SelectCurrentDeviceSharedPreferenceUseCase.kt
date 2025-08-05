package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class SelectCurrentDeviceSharedPreferenceUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    operator fun invoke(sharedPreferenceId: DeviceSharedPreferenceId) {
        val currentDevice = getCurrentDeviceIdUseCase() ?: return
        sharedPreferenceRepository.selectDeviceSharedPreference(
            deviceId = currentDevice,
            sharedPreferenceId = sharedPreferenceId,
        )
    }
}

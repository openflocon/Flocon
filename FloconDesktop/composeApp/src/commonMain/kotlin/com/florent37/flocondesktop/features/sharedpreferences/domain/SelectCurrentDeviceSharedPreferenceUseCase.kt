package com.florent37.flocondesktop.features.sharedpreferences.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceId
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

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

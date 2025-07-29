package com.florent37.flocondesktop.features.sharedpreferences.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class AskForDeviceSharedPreferencesUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val currentDeviceId = getCurrentDeviceIdUseCase() ?: return
        return sharedPreferencesRepository.askForDeviceSharedPreferences(
            deviceId = currentDeviceId,
        )
    }
}

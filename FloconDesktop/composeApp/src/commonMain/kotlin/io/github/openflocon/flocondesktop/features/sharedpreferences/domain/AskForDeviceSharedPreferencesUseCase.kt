package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

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

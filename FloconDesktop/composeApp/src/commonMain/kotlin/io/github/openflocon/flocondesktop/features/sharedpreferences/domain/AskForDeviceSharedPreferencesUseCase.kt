package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class AskForDeviceSharedPreferencesUseCase(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        return sharedPreferencesRepository.askForDeviceSharedPreferences(
            deviceIdAndPackageName = current,
        )
    }
}

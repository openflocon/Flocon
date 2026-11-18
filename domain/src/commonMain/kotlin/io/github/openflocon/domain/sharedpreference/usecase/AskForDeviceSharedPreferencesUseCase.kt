package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository

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

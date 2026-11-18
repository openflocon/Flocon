package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository

class GetCurrentDeviceSharedPreferenceValuesUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke() {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val sharedPreferenceId = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return

        sharedPreferenceRepository.getDeviceSharedPreferencesValues(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sharedPreferenceId = sharedPreferenceId.id,
        )
    }
}

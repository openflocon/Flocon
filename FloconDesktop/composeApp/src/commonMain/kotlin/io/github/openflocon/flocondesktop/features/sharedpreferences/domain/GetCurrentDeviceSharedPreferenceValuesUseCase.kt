package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

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

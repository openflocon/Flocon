package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class GetCurrentDeviceSharedPreferenceValuesUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke() {
        val rename = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val sharedPreferenceId = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return

        sharedPreferenceRepository.getDeviceSharedPreferencesValues(
            deviceIdAndPackageName = rename,
            sharedPreferenceId = sharedPreferenceId.id,
        )
    }
}

package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.DeviceSharedPreferenceId
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class SelectCurrentDeviceSharedPreferenceUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(sharedPreferenceId: DeviceSharedPreferenceId) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        sharedPreferenceRepository.selectDeviceSharedPreference(
            deviceIdAndPackageName = current,
            sharedPreferenceId = sharedPreferenceId,
        )
    }
}

package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository

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

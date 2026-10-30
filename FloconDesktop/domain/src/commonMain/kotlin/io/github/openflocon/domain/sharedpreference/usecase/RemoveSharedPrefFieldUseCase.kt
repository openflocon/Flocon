package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository

class RemoveSharedPrefFieldUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    suspend operator fun invoke(key: String) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val selectedSharedPreference = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return

        sharedPreferencesRepository.removeSharedPrefField(
            deviceIdAndPackageName = current,
            sharedPreference = selectedSharedPreference,
            key = key,
        )
    }
}

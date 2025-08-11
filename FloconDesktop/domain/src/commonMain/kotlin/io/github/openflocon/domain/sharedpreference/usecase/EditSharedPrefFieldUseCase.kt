package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository

class EditSharedPrefFieldUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    suspend operator fun invoke(
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val selectedSharedPreference = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return

        sharedPreferencesRepository.editSharedPrefField(
            deviceIdAndPackageName = current,
            sharedPreference = selectedSharedPreference,
            key = key,
            value = value,
        )
    }
}

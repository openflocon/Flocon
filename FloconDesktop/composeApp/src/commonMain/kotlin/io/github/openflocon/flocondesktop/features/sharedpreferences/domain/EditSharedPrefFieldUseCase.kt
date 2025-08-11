package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

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

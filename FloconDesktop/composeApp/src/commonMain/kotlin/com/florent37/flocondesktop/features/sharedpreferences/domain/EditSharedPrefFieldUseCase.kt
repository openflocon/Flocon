package com.florent37.flocondesktop.features.sharedpreferences.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import com.florent37.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository

class EditSharedPrefFieldUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val getCurrentDeviceSelectedSharedPreferenceUseCase: GetCurrentDeviceSelectedSharedPreferenceUseCase,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    suspend operator fun invoke(
        key: String,
        value: SharedPreferenceRowDomainModel.Value,
    ) {
        val currentDeviceId = getCurrentDeviceIdUseCase() ?: return
        val selectedSharedPreference = getCurrentDeviceSelectedSharedPreferenceUseCase() ?: return
        sharedPreferencesRepository.editSharedPrefField(
            deviceId = currentDeviceId,
            sharedPreference = selectedSharedPreference,
            key = key,
            value = value,
        )
    }
}

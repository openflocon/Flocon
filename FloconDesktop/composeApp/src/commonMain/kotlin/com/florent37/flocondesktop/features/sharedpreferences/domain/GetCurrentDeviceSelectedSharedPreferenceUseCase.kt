package com.florent37.flocondesktop.features.sharedpreferences.domain

import com.florent37.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedSharedPreferenceUseCase(
    private val observeCurrentDeviceSelectedSharedPreferenceUseCase: ObserveCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke(): DeviceSharedPreferenceDomainModel? = observeCurrentDeviceSelectedSharedPreferenceUseCase().firstOrNull()
}

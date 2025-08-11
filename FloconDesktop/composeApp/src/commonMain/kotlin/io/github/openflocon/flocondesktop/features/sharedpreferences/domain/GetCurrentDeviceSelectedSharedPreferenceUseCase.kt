package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.domain.models.DeviceSharedPreferenceDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedSharedPreferenceUseCase(
    private val observeCurrentDeviceSelectedSharedPreferenceUseCase: ObserveCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke(): DeviceSharedPreferenceDomainModel? = observeCurrentDeviceSelectedSharedPreferenceUseCase().firstOrNull()
}

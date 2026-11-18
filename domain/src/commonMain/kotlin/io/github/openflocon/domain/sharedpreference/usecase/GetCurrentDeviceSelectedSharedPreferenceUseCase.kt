package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentDeviceSelectedSharedPreferenceUseCase(
    private val observeCurrentDeviceSelectedSharedPreferenceUseCase: ObserveCurrentDeviceSelectedSharedPreferenceUseCase,
) {
    suspend operator fun invoke(): DeviceSharedPreferenceDomainModel? = observeCurrentDeviceSelectedSharedPreferenceUseCase().firstOrNull()
}

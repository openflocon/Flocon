package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceSharedPreferencesUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<DeviceSharedPreferenceDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            sharedPreferenceRepository.observeDeviceSharedPreferences(deviceId = deviceId)
        }
    }
}

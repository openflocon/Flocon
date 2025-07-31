package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSharedPreferenceValuesUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<SharedPreferenceRowDomainModel>> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(emptyList())
            } else {
                sharedPreferencesRepository
                    .observeSelectedDeviceSharedPreference(deviceId = deviceId)
                    .flatMapLatest { sharedPreference ->
                        if (sharedPreference == null) {
                            flowOf(emptyList())
                        } else {
                            sharedPreferencesRepository.observe(
                                deviceId = deviceId,
                                sharedPreferenceId = sharedPreference.id,
                            )
                        }
                    }
            }
        }
}

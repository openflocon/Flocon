package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDeviceSharedPreferencesUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<DeviceSharedPreferenceDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(emptyList())
        } else {
            sharedPreferenceRepository.observeDeviceSharedPreferences(
                deviceIdAndPackageName = current,
            )
        }
    }
}

package io.github.openflocon.domain.sharedpreference.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSelectedSharedPreferenceUseCase(
    private val sharedPreferenceRepository: SharedPreferencesRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<DeviceSharedPreferenceDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(null)
        } else {
            sharedPreferenceRepository.observeSelectedDeviceSharedPreference(
                deviceIdAndPackageName = current,
            )
        }
    }
}

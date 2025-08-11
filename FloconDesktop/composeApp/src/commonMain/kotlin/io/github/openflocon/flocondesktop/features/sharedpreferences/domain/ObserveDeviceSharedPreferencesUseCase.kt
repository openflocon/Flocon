package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
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

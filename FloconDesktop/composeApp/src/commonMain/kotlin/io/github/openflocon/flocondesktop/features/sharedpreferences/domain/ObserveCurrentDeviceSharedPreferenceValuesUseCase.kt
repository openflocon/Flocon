package io.github.openflocon.flocondesktop.features.sharedpreferences.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import com.flocon.library.domain.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceSharedPreferenceValuesUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<SharedPreferenceRowDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(emptyList())
            } else {
                sharedPreferencesRepository
                    .observeSelectedDeviceSharedPreference(deviceIdAndPackageName = current)
                    .flatMapLatest { sharedPreference ->
                        if (sharedPreference == null) {
                            flowOf(emptyList())
                        } else {
                            sharedPreferencesRepository.observe(
                                deviceIdAndPackageName = current,
                                sharedPreferenceId = sharedPreference.id,
                            )
                        }
                    }
            }
        }
}

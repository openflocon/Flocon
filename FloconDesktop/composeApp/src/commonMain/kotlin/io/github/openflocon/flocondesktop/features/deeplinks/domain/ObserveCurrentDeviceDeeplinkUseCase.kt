package io.github.openflocon.flocondesktop.features.deeplinks.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<DeeplinkDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(emptyList())
        } else {
            deeplinkRepository.observe(deviceIdAndPackageName = current)
        }
    }
}

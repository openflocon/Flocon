package io.github.openflocon.flocondesktop.features.deeplinks.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import com.florent37.flocondesktop.features.deeplinks.domain.repository.DeeplinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCurrentDeviceDeeplinkUseCase(
    private val deeplinkRepository: DeeplinkRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<DeeplinkDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
        if (deviceId == null) {
            flowOf(emptyList())
        } else {
            deeplinkRepository.observe(deviceId = deviceId)
        }
    }
}

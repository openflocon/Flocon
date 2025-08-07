package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll

class ObserveHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val observeCurrentDeviceAppUseCase: ObserveCurrentDeviceAppUseCase
) {
    operator fun invoke(): Flow<List<FloconHttpRequestDomainModel>> = combineTransform(
        observeCurrentDeviceIdUseCase(),
        observeCurrentDeviceAppUseCase()
    ) { deviceId, app ->
        if (deviceId == null || app == null) {
            emit(emptyList())
        } else {
            emitAll(networkRepository.observeRequests(deviceId, app.packageName))
        }
    }
        .distinctUntilChanged()
}

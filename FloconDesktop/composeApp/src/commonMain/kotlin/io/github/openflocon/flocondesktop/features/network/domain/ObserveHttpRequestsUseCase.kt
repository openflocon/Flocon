package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class ObserveHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase
) {
    operator fun invoke(): Flow<List<FloconHttpRequestDomainModel>> = observeCurrentDeviceIdAndPackageNameUseCase()
        .filterNotNull()
        .flatMapLatest { networkRepository.observeRequests(it) }
}

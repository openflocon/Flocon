package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Whether the selected app sent at least one network call, whatever the filters, without loading them.
 */
class ObserveCurrentDeviceHasNetworkRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<Boolean> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(false)
            } else {
                networkRepository.observeHasRequests(current)
            }
        }
        .distinctUntilChanged()
}

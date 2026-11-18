package io.github.openflocon.domain.network.usecase

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkRequestsUseCase(
    private val networkRepository: NetworkRepository,
) {
    operator fun invoke(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel?,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<PagingData<FloconNetworkCallDomainModel>> = if (deviceIdAndPackageName == null) {
        flowOf(PagingData.empty())
    } else {
        networkRepository.observeRequests(
            deviceIdAndPackageName = deviceIdAndPackageName,
            sortedBy = sortedBy,
            filter = filter,
        ).distinctUntilChanged()
    }
}

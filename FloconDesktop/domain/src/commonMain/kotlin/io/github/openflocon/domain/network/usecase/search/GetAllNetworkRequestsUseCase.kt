package io.github.openflocon.domain.network.usecase.search

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository

class GetAllNetworkRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(): List<FloconNetworkCallDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return emptyList()
        return networkRepository.getRequests(
            deviceIdAndPackageName = current,
            sortedBy = null,
            filter = NetworkFilterDomainModel(
                filterOnAllColumns = null,
                textsFilters = null,
                methodFilter = null,
                displayOldSessions = true // Include everything for global search
            ),
        )
    }
}

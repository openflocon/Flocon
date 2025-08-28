package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository

class GetNetworkFilterUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(column: NetworkTextFilterColumns): TextFilterStateDomainModel = getCurrentDeviceIdAndPackageNameUseCase()?.let { current ->
        networkFilterRepository.get(deviceAndApp = current, column = column)
    } ?: TextFilterStateDomainModel(
        items = listOf(),
        isEnabled = true,
    )
}

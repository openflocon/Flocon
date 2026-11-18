package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository

class UpdateNetworkFilterUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { current ->
            networkFilterRepository.update(
                deviceAndApp = current,
                column = column,
                newValue = newValue,
            )
        }
    }
}

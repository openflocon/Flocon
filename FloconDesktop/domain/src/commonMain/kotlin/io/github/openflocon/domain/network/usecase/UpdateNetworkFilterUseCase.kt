package io.github.openflocon.domain.network.usecase

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.repository.NetworkFilterRepository

class UpdateNetworkFilterUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) {
        getCurrentDeviceIdUseCase()?.let { deviceId ->
            networkFilterRepository.update(
                deviceId = deviceId,
                column = column,
                newValue = newValue,
            )
        }
    }
}

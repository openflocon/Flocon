package io.github.openflocon.flocondesktop.features.network.domain.filter

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkFilterRepository

class UpdateNetworkFilterUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel
    ) {
        getCurrentDeviceIdUseCase()?.let { deviceId ->
            networkFilterRepository.update(
                deviceId = deviceId,
                column = column,
                newValue = newValue
            )
        }
    }
}

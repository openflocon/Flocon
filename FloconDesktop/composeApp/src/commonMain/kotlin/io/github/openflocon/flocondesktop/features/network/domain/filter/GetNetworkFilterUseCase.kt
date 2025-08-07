package io.github.openflocon.flocondesktop.features.network.domain.filter

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkFilterRepository

class GetNetworkFilterUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(column: NetworkTextFilterColumns): TextFilterStateDomainModel {
        return getCurrentDeviceIdUseCase()?.let { deviceId ->
            networkFilterRepository.get(deviceId = deviceId, column = column)
        } ?: TextFilterStateDomainModel(
            items = listOf(),
            isEnabled = true,
        )
    }
}

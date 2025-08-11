package io.github.openflocon.domain.network.usecase

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.repository.NetworkFilterRepository

class GetNetworkFilterUseCase(
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    suspend operator fun invoke(column: NetworkTextFilterColumns): TextFilterStateDomainModel = getCurrentDeviceIdUseCase()?.let { deviceId ->
        networkFilterRepository.get(deviceId = deviceId, column = column)
    } ?: TextFilterStateDomainModel(
        items = listOf(),
        isEnabled = true,
    )
}

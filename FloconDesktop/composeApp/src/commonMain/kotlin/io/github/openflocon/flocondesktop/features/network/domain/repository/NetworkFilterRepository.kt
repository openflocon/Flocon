package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkFilterRepository {
    suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns
    ): TextFilterStateDomainModel?

    fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>>
    suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel
    )
}

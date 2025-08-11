package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.domain.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkFilterRepository {
    suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel?

    fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>>
    suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    )
}

package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.domain.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkFilterLocalDataSource {
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

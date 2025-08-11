package io.github.openflocon.flocondesktop.features.network.domain.repository

import com.flocon.library.domain.models.DeviceId
import com.flocon.library.domain.models.NetworkTextFilterColumns
import com.flocon.library.domain.models.TextFilterStateDomainModel
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

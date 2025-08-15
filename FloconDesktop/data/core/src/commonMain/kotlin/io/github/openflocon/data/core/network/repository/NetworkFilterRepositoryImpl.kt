package io.github.openflocon.data.core.network.repository

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkFilterRepositoryImpl(
    private val networkFilterLocalDataSource: NetworkFilterLocalDataSource,
) : NetworkFilterRepository {
    override suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? = networkFilterLocalDataSource.get(
        deviceId = deviceId,
        column = column,
    )

    override fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> = networkFilterLocalDataSource.observe(
        deviceId = deviceId,
    ).distinctUntilChanged()

    override suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) = networkFilterLocalDataSource.update(
        deviceId = deviceId,
        column = column,
        newValue = newValue,
    )
}

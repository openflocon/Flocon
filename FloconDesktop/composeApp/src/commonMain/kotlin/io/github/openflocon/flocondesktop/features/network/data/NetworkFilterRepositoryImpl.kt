package io.github.openflocon.flocondesktop.features.network.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.NetworkFilterLocalDataSource
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkFilterRepositoryImpl(
    private val networkFilterLocalDataSource: NetworkFilterLocalDataSource,
) : NetworkFilterRepository {
    override suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? {
        return networkFilterLocalDataSource.get(
            deviceId = deviceId,
            column = column,
        )
    }

    override fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> {
        return networkFilterLocalDataSource.observe(
            deviceId = deviceId,
        ).distinctUntilChanged()
    }

    override suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel
    ) {
        return networkFilterLocalDataSource.update(
            deviceId = deviceId,
            column = column,
            newValue = newValue,
        )
    }
}

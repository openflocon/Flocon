package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.textFilterToDomain
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.textFilterToEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.NetworkFilterEntity
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class NetworkFilterLocalDataSourceRoom(
    private val networkFilterDao: NetworkFilterDao,
) : NetworkFilterLocalDataSource {

    override suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns
    ): TextFilterStateDomainModel? {
        return networkFilterDao.get(
            deviceId = deviceId,
            column = column,
        )?.let {
            textFilterToDomain(it)
        }
    }

    override fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> {
        return networkFilterDao.observe(
            deviceId = deviceId,
        ).map {
            it.associate {
                it.columnName to textFilterToDomain(it)
            }
        }
    }

    override suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel
    ) {
        networkFilterDao.insertOrUpdate(
            textFilterToEntity(
                deviceId = deviceId,
                column = column,
                domain = newValue,
            )
        )
    }
}

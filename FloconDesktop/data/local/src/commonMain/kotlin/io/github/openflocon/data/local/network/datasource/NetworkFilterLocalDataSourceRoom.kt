package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkFilterDao
import io.github.openflocon.data.local.network.mapper.textFilterToDomain
import io.github.openflocon.data.local.network.mapper.textFilterToEntity
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkFilterLocalDataSourceRoom(
    private val networkFilterDao: NetworkFilterDao,
) : NetworkFilterLocalDataSource {

    override suspend fun get(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? = networkFilterDao.get(
        deviceId = deviceId,
        column = column,
    )?.let {
        textFilterToDomain(it)
    }

    override fun observe(deviceId: DeviceId): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> = networkFilterDao.observe(
        deviceId = deviceId,
    ).map {
        it.associate {
            it.columnName to textFilterToDomain(it)
        }
    }

    override suspend fun update(
        deviceId: DeviceId,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) {
        networkFilterDao.insertOrUpdate(
            textFilterToEntity(
                deviceId = deviceId,
                column = column,
                domain = newValue,
            ),
        )
    }
}

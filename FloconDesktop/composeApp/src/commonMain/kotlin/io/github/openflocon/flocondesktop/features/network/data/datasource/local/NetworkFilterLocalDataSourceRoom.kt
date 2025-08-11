package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.textFilterToDomain
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.mapper.textFilterToEntity
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.models.TextFilterStateDomainModel
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

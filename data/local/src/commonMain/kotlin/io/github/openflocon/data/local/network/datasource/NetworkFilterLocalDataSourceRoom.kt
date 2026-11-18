package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkFilterLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkFilterDao
import io.github.openflocon.data.local.network.mapper.textFilterToDomain
import io.github.openflocon.data.local.network.mapper.textFilterToEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class NetworkFilterLocalDataSourceRoom(
    private val networkFilterDao: NetworkFilterDao,
    private val json: Json,
) : NetworkFilterLocalDataSource {

    override suspend fun get(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
    ): TextFilterStateDomainModel? = networkFilterDao.get(
        deviceId = deviceAndApp.deviceId,
        packageName = deviceAndApp.packageName,
        column = column,
    )?.textFilterToDomain(
        json = json,
    )

    override fun observe(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
    ): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> = networkFilterDao.observe(
        deviceId = deviceAndApp.deviceId,
        packageName = deviceAndApp.packageName,
    ).map {
        it.associate {
            it.columnName to it.textFilterToDomain(
                json = json,
            )
        }
    }

    override suspend fun update(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        column: NetworkTextFilterColumns,
        newValue: TextFilterStateDomainModel,
    ) {
        networkFilterDao.insertOrUpdate(
            textFilterToEntity(
                deviceId = deviceAndApp.deviceId,
                packageName = deviceAndApp.packageName,
                column = column,
                domain = newValue,
                json = json,
            ),
        )
    }
}

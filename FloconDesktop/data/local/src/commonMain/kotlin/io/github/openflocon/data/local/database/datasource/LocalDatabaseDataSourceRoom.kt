package io.github.openflocon.data.local.database.datasource

import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.local.database.dao.QueryDao
import io.github.openflocon.data.local.database.dao.TablesDao
import io.github.openflocon.data.local.database.mapper.toDomain
import io.github.openflocon.data.local.database.models.SuccessQueryEntity
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal class LocalDatabaseDataSourceRoom(
    private val successQueryDao: QueryDao,
    private val tablesDao: TablesDao,
    private val json: Json,
) : LocalDatabaseDataSource {

    override suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String
    ) {
        if (successQueryDao.doesQueryExists(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                databaseId = databaseId,
                queryString = query,
            )
        ) {
            // put it in top if we already have it
            successQueryDao.deleteQuery(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
                databaseId = databaseId,
                queryString = query,
            )
        }

        val entity = SuccessQueryEntity(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            queryString = query,
            timestamp = System.currentTimeMillis(),
        )
        successQueryDao.insertSuccessQuery(entity)
    }

    override fun observeLastSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId
    ): Flow<List<String>> = successQueryDao.observeSuccessQueriesByDeviceId(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        databaseId = databaseId,
    )

    override suspend fun saveTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        table: DatabaseTableDomainModel
    ) {
        tablesDao.insertTable(
            table = table.toEntity(
                databaseId = databaseId,
                deviceIdAndPackageName = deviceIdAndPackageName,
                json = json,
            )
        )
    }

    override suspend fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<DatabaseTableDomainModel>> {
        return tablesDao.observe(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
        ).map {
            it.map {
                it.toDomain(
                    json = json,
                )
            }
        }
    }

    override suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>
    ) {
        tablesDao.removeTablesNotPresentAnymore(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            databaseId = databaseId,
            tablesNames = tablesNames,
        )
    }
}

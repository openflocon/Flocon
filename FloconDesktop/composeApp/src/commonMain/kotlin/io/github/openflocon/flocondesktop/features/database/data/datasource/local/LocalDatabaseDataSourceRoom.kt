package io.github.openflocon.flocondesktop.features.database.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import kotlinx.coroutines.flow.Flow

class LocalDatabaseDataSourceRoom(private val successQueryDao: QueryDao) : LocalDatabaseDataSource {

    override suspend fun saveSuccessQuery(deviceId: DeviceId, databaseId: DeviceDataBaseId, query: String) {
        if (successQueryDao.doesQueryExists(deviceId = deviceId, databaseId = databaseId, queryString = query)) {
            // put it in top if we already have it
            successQueryDao.deleteQuery(deviceId = deviceId, databaseId = databaseId, queryString = query)
        }

        val entity = SuccessQueryEntity(
            deviceId = deviceId,
            databaseId = databaseId,
            queryString = query,
            timestamp = System.currentTimeMillis(),
        )
        successQueryDao.insertSuccessQuery(entity)
    }

    override fun observeLastSuccessQuery(deviceId: DeviceId, databaseId: DeviceDataBaseId): Flow<List<String>> = successQueryDao.observeSuccessQueriesByDeviceId(
        deviceId = deviceId,
        databaseId = databaseId,
    )
}

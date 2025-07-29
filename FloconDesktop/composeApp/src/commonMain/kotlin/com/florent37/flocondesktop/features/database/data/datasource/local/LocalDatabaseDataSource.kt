package com.florent37.flocondesktop.features.database.data.datasource.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseId
import kotlinx.coroutines.flow.Flow

interface LocalDatabaseDataSource {
    suspend fun saveSuccessQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    )
    fun observeLastSuccessQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
    ): Flow<List<String>>
}

package io.github.openflocon.flocondesktop.features.database.data.datasource.local

import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface LocalDatabaseDataSource {
    suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
        query: String,
    )

    fun observeLastSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId
    ): Flow<List<String>>
}

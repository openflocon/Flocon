package io.github.openflocon.data.core.database.datasource

import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalDatabaseDataSource {
    suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    )

    fun observeLastSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<String>>
}

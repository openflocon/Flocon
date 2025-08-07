package io.github.openflocon.flocondesktop.features.database.domain.repository

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DeviceDataBaseDomainModel>>

    fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DeviceDataBaseDomainModel?>

    fun selectDeviceDatabase(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
    )

    suspend fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databases: List<DeviceDataBaseDomainModel>,
    )

    suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName)

    suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel>

    suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
        query: String,
    )

    fun observeLastSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageName, databaseId: DeviceDataBaseId): Flow<List<String>>
}

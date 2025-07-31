package io.github.openflocon.flocondesktop.features.database.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun observeDeviceDatabases(deviceId: DeviceId): Flow<List<DeviceDataBaseDomainModel>>

    fun observeSelectedDeviceDatabase(deviceId: DeviceId): Flow<DeviceDataBaseDomainModel?>

    fun selectDeviceDatabase(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
    )

    suspend fun registerDeviceDatabases(
        deviceId: DeviceId,
        databases: List<DeviceDataBaseDomainModel>,
    )

    suspend fun askForDeviceDatabases(deviceId: DeviceId)

    suspend fun executeQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel>

    suspend fun saveSuccessQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    )
    fun observeLastSuccessQuery(deviceId: DeviceId, databaseId: DeviceDataBaseId): Flow<List<String>>
}

package io.github.openflocon.flocondesktop.features.database.domain.repository

import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>>

    fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceDataBaseDomainModel?>

    fun selectDeviceDatabase(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    )

    suspend fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databases: List<DeviceDataBaseDomainModel>,
    )

    suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel>

    suspend fun saveSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    )

    fun observeLastSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId): Flow<List<String>>
}

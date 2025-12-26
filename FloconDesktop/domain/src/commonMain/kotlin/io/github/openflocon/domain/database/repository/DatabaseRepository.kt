package io.github.openflocon.domain.database.repository

import androidx.paging.PagingData
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>>

    suspend fun getDatabaseById(databaseId: String): DeviceDataBaseDomainModel?

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

    suspend fun saveTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        table: DatabaseTableDomainModel,
    )

    suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>,
    )

    suspend fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<DatabaseTableDomainModel>>

    suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String,
    ): Either<Throwable, Unit>

    suspend fun deleteFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long,
    ): Either<Throwable, Unit>

    fun observeFavorites(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<DatabaseFavoriteQueryDomainModel>>

    suspend fun getFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): DatabaseFavoriteQueryDomainModel?

    fun observeQueryLogs(dbName: String): Flow<PagingData<DatabaseQueryLogDomainModel>>
}

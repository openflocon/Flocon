package io.github.openflocon.data.core.database.datasource

import androidx.paging.PagingData
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
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

    suspend fun saveTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        table: DatabaseTableDomainModel
    )

    suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>
    )

    suspend fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId
    ): Flow<List<DatabaseTableDomainModel>>

    suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String
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

    suspend fun saveQueryLog(
        log: DatabaseQueryLogDomainModel,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    )

    fun observeQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>>

    fun countQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): Flow<Int>

    suspend fun getQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        dbName: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>
    ): List<DatabaseQueryLogDomainModel>

    suspend fun deleteAllQueryLogs(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    )
}

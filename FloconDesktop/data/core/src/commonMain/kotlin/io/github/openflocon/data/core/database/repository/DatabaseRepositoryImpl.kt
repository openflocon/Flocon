package io.github.openflocon.data.core.database.repository

import androidx.paging.PagingData
import io.github.openflocon.data.core.database.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.core.database.datasource.QueryDatabaseRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.FilterQueryLogDomainModel
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

class DatabaseRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val deviceDatabasesDataSource: DeviceDatabasesRemoteDataSource,
    private val queryDatabaseDataSource: QueryDatabaseRemoteDataSource,
    private val localDatabaseDataSource: LocalDatabaseDataSource,
) : DatabaseRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Database.Plugin)

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>> = deviceDatabasesDataSource
        .observeDeviceDatabases(deviceIdAndPackageName)
        .flowOn(dispatcherProvider.data)

    override suspend fun getDatabaseById(databaseId: String): DeviceDataBaseDomainModel? = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.getDatabaseById(databaseId)
    }

    override suspend fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databases: List<DeviceDataBaseDomainModel>,
    ) = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.registerDeviceDatabases(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databases = databases,
        )
    }

    override suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.askForDeviceDatabases(deviceIdAndPackageName = deviceIdAndPackageName)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> = withContext(dispatcherProvider.data) {
        queryDatabaseDataSource.executeQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            query = query,
        )
    }

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Database.Method.Query -> {
                    queryDatabaseDataSource.getReceiveQuery(message)
                        ?.let { queryDatabaseDataSource.onQueryResultReceived(received = it) }
                }

                Protocol.FromDevice.Database.Method.GetDatabases -> {
                    val items = queryDatabaseDataSource.getDeviceDatabases(message)

                    deviceDatabasesDataSource.registerDeviceDatabases(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        databases = items,
                    )
                }

                Protocol.FromDevice.Database.Method.LogQuery -> {
                    queryDatabaseDataSource.getQueryLogs(
                        message,
                    )?.let {
                        // the name is only displayed, it falls back to the id when the database is not listed (yet)
                        val dbName = deviceDatabasesDataSource.getDatabaseById(it.dbId)?.name ?: it.dbId
                        localDatabaseDataSource.saveQueryLog(
                            it.copy(dbName = dbName),
                            deviceIdAndPackageName = deviceIdAndPackageName
                        )
                    }
                }
            }
        }
    }

    override suspend fun saveSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId, query: String) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.saveSuccessQuery(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                query = query,
            )
        }
    }

    override fun observeLastSuccessQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<String>> = localDatabaseDataSource.observeLastSuccessQuery(
        deviceIdAndPackageName = deviceIdAndPackageName,
        databaseId = databaseId,
    )
        .flowOn(dispatcherProvider.data)

    override suspend fun saveTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        table: DatabaseTableDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.saveTable(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                table = table,
            )
        }
    }

    override suspend fun removeTablesNotPresentAnymore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        tablesNames: List<String>,
    ) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.removeTablesNotPresentAnymore(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                tablesNames = tablesNames,
            )
        }
    }

    override suspend fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ): Flow<List<DatabaseTableDomainModel>> = localDatabaseDataSource.observe(
        deviceIdAndPackageName = deviceIdAndPackageName,
        databaseId = databaseId,
    ).flowOn(dispatcherProvider.data)

    override suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String,
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        localDatabaseDataSource.saveAsFavorite(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            title = title,
            query = query,
        )
    }

    override suspend fun deleteFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): Either<Throwable, Unit> = withContext(dispatcherProvider.data) {
        localDatabaseDataSource.deleteFavorite(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            id = id,
        )
    }

    override fun observeFavorites(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DatabaseFavoriteQueryDomainModel>> = localDatabaseDataSource.observeFavorites(
        deviceIdAndPackageName = deviceIdAndPackageName,
    ).flowOn(dispatcherProvider.data)

    override suspend fun getFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): DatabaseFavoriteQueryDomainModel? = withContext(dispatcherProvider.data) {
        localDatabaseDataSource.getFavorite(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            id = id,
        )
    }

    override fun observeQueryLogs(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        limit: Int,
        offset: Int,
    ): Flow<List<DatabaseQueryLogDomainModel>> = localDatabaseDataSource.observeQueryLogs(
        dbId = dbId,
        showTransactions = showTransactions,
        filters = filters,
        deviceIdAndPackageName = deviceIdAndPackageName,
        limit = limit,
        offset = offset,
    )

    override fun countQueryLogs(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<Int> = localDatabaseDataSource.countQueryLogs(
        dbId = dbId,
        showTransactions = showTransactions,
        filters = filters,
        deviceIdAndPackageName = deviceIdAndPackageName,
    )

    override suspend fun getQueryLogs(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<DatabaseQueryLogDomainModel> = localDatabaseDataSource.getQueryLogs(
        dbId = dbId,
        showTransactions = showTransactions,
        filters = filters,
        deviceIdAndPackageName = deviceIdAndPackageName,
    )

    override suspend fun getLatestQueryLogs(
        dbId: String,
        showTransactions: Boolean,
        filters: List<FilterQueryLogDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        appInstance: Long?,
        limit: Int,
    ): List<DatabaseQueryLogDomainModel> = withContext(dispatcherProvider.data) {
        localDatabaseDataSource.getLatestQueryLogs(
            deviceIdAndPackageName = deviceIdAndPackageName,
            dbId = dbId,
            showTransactions = showTransactions,
            filters = filters,
            appInstance = appInstance,
            limit = limit,
        )
    }

    override fun observeQueryLog(id: Int): Flow<DatabaseQueryLogDomainModel?> = localDatabaseDataSource.observeQueryLog(id)

    override suspend fun deleteAllQueryLogs(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.deleteAllQueryLogs(deviceIdAndPackageName)
        }
    }
}

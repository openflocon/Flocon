package io.github.openflocon.data.core.database.repository

import io.github.openflocon.data.core.database.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.core.database.datasource.QueryDatabaseRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.repository.DatabaseRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    override suspend fun getDatabaseById(databaseId: String): DeviceDataBaseDomainModel? {
        return withContext(dispatcherProvider.data) {
            deviceDatabasesDataSource.getDatabaseById(databaseId)
        }
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
    ): Flow<List<DatabaseTableDomainModel>> {
        return localDatabaseDataSource.observe(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun saveAsFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        title: String,
        query: String,
    ): Either<Throwable, Unit> {
        return withContext(dispatcherProvider.data) {
            localDatabaseDataSource.saveAsFavorite(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                title = title,
                query = query,
            )
        }
    }

    override suspend fun deleteFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): Either<Throwable, Unit> {
        return withContext(dispatcherProvider.data) {
            localDatabaseDataSource.deleteFavorite(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                id = id,
            )
        }
    }

    override fun observeFavorites(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DatabaseFavoriteQueryDomainModel>> {
        return localDatabaseDataSource.observeFavorites(
            deviceIdAndPackageName = deviceIdAndPackageName,
        ).flowOn(dispatcherProvider.data)
    }

    override suspend fun getFavorite(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: String,
        id: Long
    ): DatabaseFavoriteQueryDomainModel? {
        return withContext(dispatcherProvider.data) {
            localDatabaseDataSource.getFavorite(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                id = id,
            )
        }
    }
}

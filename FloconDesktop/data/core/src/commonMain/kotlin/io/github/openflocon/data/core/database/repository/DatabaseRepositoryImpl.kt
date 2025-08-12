package io.github.openflocon.data.core.database.repository

import io.github.openflocon.data.core.database.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.data.core.database.datasource.LocalDatabaseDataSource
import io.github.openflocon.data.core.database.datasource.QueryDatabaseRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
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

    override fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceDataBaseDomainModel?> =
        deviceDatabasesDataSource
            .observeSelectedDeviceDatabase(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun selectDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, databaseId: DeviceDataBaseId) {
        deviceDatabasesDataSource.selectDeviceDatabase(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
        )
    }

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>> =
        deviceDatabasesDataSource
            .observeDeviceDatabases(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override suspend fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databases: List<DeviceDataBaseDomainModel>
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

    override suspend fun onMessageReceived(deviceId: String, message: FloconIncomingMessageDomainModel) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Database.Method.Query -> {
                    queryDatabaseDataSource.getReceiveQuery(message)
                        ?.let { queryDatabaseDataSource.onQueryResultReceived(received = it) }
                }

                Protocol.FromDevice.Database.Method.GetDatabases -> {
                    val items = queryDatabaseDataSource.getDeviceDatabases(message)

                    deviceDatabasesDataSource.registerDeviceDatabases(
                        deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                            deviceId = deviceId,
                            packageName = message.appPackageName,
                        ),
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
        databaseId: DeviceDataBaseId
    ): Flow<List<String>> = localDatabaseDataSource.observeLastSuccessQuery(
        deviceIdAndPackageName = deviceIdAndPackageName,
        databaseId = databaseId,
    )
        .flowOn(dispatcherProvider.data)
}

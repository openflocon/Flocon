package io.github.openflocon.flocondesktop.features.database.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.DeviceDatabasesDataSource
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.QueryDatabaseDataSource
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSource
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.toDeviceDatabasesDomain
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

class DatabaseRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val deviceDatabasesDataSource: DeviceDatabasesDataSource,
    private val queryDatabaseDataSource: QueryDatabaseDataSource,
    private val localDatabaseDataSource: LocalDatabaseDataSource,
) : DatabaseRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Database.Plugin)

    override fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DeviceDataBaseDomainModel?> =
        deviceDatabasesDataSource
            .observeSelectedDeviceDatabase(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun selectDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageName, databaseId: DeviceDataBaseId) {
        deviceDatabasesDataSource.selectDeviceDatabase(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
        )
    }

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DeviceDataBaseDomainModel>> =
        deviceDatabasesDataSource
            .observeDeviceDatabases(deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override suspend fun registerDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName, databases: List<DeviceDataBaseDomainModel>) =
        withContext(dispatcherProvider.data) {
            deviceDatabasesDataSource.registerDeviceDatabases(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databases = databases,
            )
        }

    override suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName) = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.askForDeviceDatabases(deviceIdAndPackageName = deviceIdAndPackageName)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
        query: String
    ): Either<Throwable, DatabaseExecuteSqlResponseDomainModel> = withContext(dispatcherProvider.data) {
        queryDatabaseDataSource.executeQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId,
            query = query,
        )
    }

    override suspend fun onMessageReceived(
        deviceId: DeviceId,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Database.Method.Query ->
                    decodeReceivedQuery(message.body)
                        ?.let { received ->
                            queryDatabaseDataSource.onQueryResultReceived(
                                received = received,
                            )
                        }

                Protocol.FromDevice.Database.Method.GetDatabases ->
                    decodeDeviceDatabases(message.body)
                        ?.let { toDeviceDatabasesDomain(it) }
                        ?.let {
                            deviceDatabasesDataSource.registerDeviceDatabases(
                                deviceIdAndPackageName = DeviceIdAndPackageName(
                                    deviceId = deviceId,
                                    packageName = message.appPackageName
                                ),
                                databases = it,
                            )
                        }
            }
        }
    }

    override suspend fun saveSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageName, databaseId: DeviceDataBaseId, query: String) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.saveSuccessQuery(
                deviceIdAndPackageName = deviceIdAndPackageName,
                databaseId = databaseId,
                query = query,
            )
        }
    }

    override fun observeLastSuccessQuery(deviceIdAndPackageName: DeviceIdAndPackageName, databaseId: DeviceDataBaseId): Flow<List<String>> =
        localDatabaseDataSource.observeLastSuccessQuery(
            deviceIdAndPackageName = deviceIdAndPackageName,
            databaseId = databaseId
        )
            .flowOn(dispatcherProvider.data)
}

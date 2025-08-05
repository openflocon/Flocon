package io.github.openflocon.flocondesktop.features.database.data

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.DeviceDatabasesDataSource
import io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases.QueryDatabaseDataSource
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.LocalDatabaseDataSource
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.toDeviceDatabasesDomain
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.domain.repository.DatabaseRepository
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

    override fun observeSelectedDeviceDatabase(deviceId: DeviceId) = deviceDatabasesDataSource
        .observeSelectedDeviceDatabase(deviceId)
        .flowOn(dispatcherProvider.data)

    override fun selectDeviceDatabase(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
    ) {
        deviceDatabasesDataSource.selectDeviceDatabase(
            deviceId = deviceId,
            databaseId = databaseId,
        )
    }

    override fun observeDeviceDatabases(deviceId: DeviceId) = deviceDatabasesDataSource
        .observeDeviceDatabases(deviceId)
        .flowOn(dispatcherProvider.data)

    override suspend fun registerDeviceDatabases(
        deviceId: DeviceId,
        databases: List<DeviceDataBaseDomainModel>,
    ) = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.registerDeviceDatabases(
            deviceId = deviceId,
            databases = databases,
        )
    }

    override suspend fun askForDeviceDatabases(deviceId: DeviceId) = withContext(dispatcherProvider.data) {
        deviceDatabasesDataSource.askForDeviceDatabases(deviceId = deviceId)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun executeQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    ) = withContext(dispatcherProvider.data) {
        queryDatabaseDataSource.executeQuery(
            deviceId = deviceId,
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
                                deviceId = deviceId,
                                databases = it,
                            )
                        }
            }
        }
    }

    override suspend fun saveSuccessQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    ) {
        withContext(dispatcherProvider.data) {
            localDatabaseDataSource.saveSuccessQuery(
                deviceId = deviceId,
                databaseId = databaseId,
                query = query,
            )
        }
    }

    override fun observeLastSuccessQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
    ): Flow<List<String>> = localDatabaseDataSource.observeLastSuccessQuery(deviceId = deviceId, databaseId = databaseId)
        .flowOn(dispatcherProvider.data)
}

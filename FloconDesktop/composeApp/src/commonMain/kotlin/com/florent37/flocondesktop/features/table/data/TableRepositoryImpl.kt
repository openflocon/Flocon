package com.florent37.flocondesktop.features.table.data

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.table.data.datasource.RemoteTableDataSource
import com.florent37.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSource
import com.florent37.flocondesktop.features.table.data.datasource.local.TableLocalDataSource
import com.florent37.flocondesktop.features.table.data.mapper.toDomain
import com.florent37.flocondesktop.features.table.data.model.TableItemDataModel
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import com.florent37.flocondesktop.features.table.domain.repository.TableRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class TableRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val tableLocalDataSource: TableLocalDataSource,
    private val deviceTablesDataSource: DeviceTablesDataSource,
    private val remoteTableDataSource: RemoteTableDataSource,
) : TableRepository,
    MessagesReceiverRepository {

    // maybe inject
    private val tableParser =
        Json {
            ignoreUnknownKeys = true
        }

    override val pluginName = listOf(Protocol.FromDevice.Table.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Table.Method.AddItems -> {
                    decodeAddItems(message)
                        .takeIf { it.isNotEmpty() }
                        ?.let { list -> list.map { toDomain(it) } }
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { table ->
                            tableLocalDataSource.insert(
                                deviceId = deviceId,
                                tablePartialInfos = table,
                            )
                            remoteTableDataSource.clearReceivedItem(
                                deviceId = deviceId,
                                items = table.flatMap { it.items.map { it.itemId } },
                            )
                        }
                }
            }
        }
    }

    private fun decodeAddItems(message: FloconIncomingMessageDataModel): List<TableItemDataModel> = try {
        tableParser.decodeFromString<List<TableItemDataModel>>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }

    override fun observeTable(deviceId: DeviceId, tableId: TableId): Flow<TableDomainModel?> = tableLocalDataSource.observe(deviceId = deviceId, tableId = tableId)
        .flowOn(dispatcherProvider.data)

    override suspend fun deleteTable(
        deviceId: DeviceId,
        tableId: TableIdentifierDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            tableLocalDataSource.delete(deviceId = deviceId, tableId = tableId)
        }
    }

    override suspend fun selectDeviceTable(
        deviceId: DeviceId,
        tableId: TableId,
    ) {
        deviceTablesDataSource.selectDeviceTable(
            deviceId = deviceId,
            tableId = tableId,
            deviceTables = tableLocalDataSource.getDeviceTables(deviceId = deviceId),
        )
    }

    override fun observeSelectedDeviceTable(deviceId: DeviceId): Flow<TableIdentifierDomainModel?> = deviceTablesDataSource.observeSelectedDeviceTable(deviceId = deviceId)
        .flowOn(dispatcherProvider.data)

    override fun observeDeviceTables(deviceId: DeviceId): Flow<List<TableIdentifierDomainModel>> = tableLocalDataSource.observeDeviceTables(deviceId = deviceId)
        .flowOn(dispatcherProvider.data)
}

package io.github.openflocon.flocondesktop.features.table.data

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.table.data.datasource.RemoteTableDataSource
import io.github.openflocon.flocondesktop.features.table.data.datasource.device.DeviceTablesDataSource
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.TableLocalDataSource
import io.github.openflocon.flocondesktop.features.table.data.mapper.toDomain
import io.github.openflocon.flocondesktop.features.table.data.model.TableItemDataModel
import com.flocon.library.domain.models.TableDomainModel
import com.flocon.library.domain.models.TableId
import com.flocon.library.domain.models.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.features.table.domain.repository.TableRepository
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
                            val current = DeviceIdAndPackageNameDomainModel(
                                deviceId = deviceId,
                                packageName = message.appPackageName,
                            )
                            tableLocalDataSource.insert(
                                deviceIdAndPackageName = current,
                                tablePartialInfos = table,
                            )
                            remoteTableDataSource.clearReceivedItem(
                                deviceIdAndPackageName = current,
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

    override fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?> =
        tableLocalDataSource.observe(deviceIdAndPackageName = deviceIdAndPackageName, tableId = tableId)
            .flowOn(dispatcherProvider.data)

    override suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel) {
        withContext(dispatcherProvider.data) {
            tableLocalDataSource.delete(deviceIdAndPackageName = deviceIdAndPackageName, tableId = tableId)
        }
    }

    override suspend fun selectDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId) {
        deviceTablesDataSource.selectDeviceTable(
            deviceIdAndPackageName = deviceIdAndPackageName,
            tableId = tableId,
            deviceTables = tableLocalDataSource.getDeviceTables(deviceIdAndPackageName = deviceIdAndPackageName),
        )
    }

    override fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?> =
        deviceTablesDataSource.observeSelectedDeviceTable(deviceIdAndPackageName = deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)

    override fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>> =
        tableLocalDataSource.observeDeviceTables(deviceIdAndPackageName = deviceIdAndPackageName)
            .flowOn(dispatcherProvider.data)
}

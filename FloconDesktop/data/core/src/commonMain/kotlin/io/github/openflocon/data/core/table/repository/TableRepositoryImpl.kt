package io.github.openflocon.data.core.table.repository

import io.github.openflocon.data.core.table.datasource.DeviceTablesDataSource
import io.github.openflocon.data.core.table.datasource.TableLocalDataSource
import io.github.openflocon.data.core.table.datasource.TableRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.domain.table.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TableRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val tableLocalDataSource: TableLocalDataSource,
    private val deviceTablesDataSource: DeviceTablesDataSource,
    private val remoteTableDataSource: TableRemoteDataSource,
) : TableRepository,
    MessagesReceiverRepository {

    override val pluginName = listOf(Protocol.FromDevice.Table.Plugin)

    override suspend fun onMessageReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        message: FloconIncomingMessageDomainModel
    ) {
        withContext(dispatcherProvider.data) {
            when (message.method) {
                Protocol.FromDevice.Table.Method.AddItems -> {
                    val items = remoteTableDataSource.getItems(message)
                    tableLocalDataSource.insert(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        tablePartialInfos = items,
                    )
                    remoteTableDataSource.clearReceivedItem(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        items = items.flatMap { it.items.map { it.itemId } },
                    )
                }
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }

    override fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?> =
        tableLocalDataSource.observe(deviceIdAndPackageName = deviceIdAndPackageName, tableId = tableId)
            .flowOn(dispatcherProvider.data)

    override suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel) {
        withContext(dispatcherProvider.data) {
            tableLocalDataSource.delete(deviceIdAndPackageName = deviceIdAndPackageName, tableId = tableId)
        }
    }

    override suspend fun deleteItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, itemId: String) {
        withContext(dispatcherProvider.data) {
            tableLocalDataSource.deleteItem(
                deviceIdAndPackageName = deviceIdAndPackageName,
                itemId = itemId,
            )
        }
    }
    override suspend fun deleteBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, itemId: String) {
        withContext(dispatcherProvider.data) {
            tableLocalDataSource.deleteBefore(deviceIdAndPackageName = deviceIdAndPackageName, itemId = itemId)
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

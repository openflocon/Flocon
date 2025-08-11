package io.github.openflocon.flocondesktop.features.table.data.datasource.local

import io.github.openflocon.data.core.table.datasource.TableLocalDataSource
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.mapper.toEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TableLocalDataSourceRoom(
    private val tableDao: FloconTableDao,
    private val dispatcherProvider: DispatcherProvider,
) : TableLocalDataSource {

    override suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tablePartialInfos: List<TableDomainModel>) {
        withContext(dispatcherProvider.data) {
            tablePartialInfos.forEach { tableInfo ->
                var tableId = tableDao.insertTable(tableInfo.toEntity(deviceIdAndPackageName))
                if (tableId == -1L) {
                    // table already exists
                    tableId = tableDao.getTableId(
                        deviceId = deviceIdAndPackageName.deviceId,
                        packageName = deviceIdAndPackageName.packageName,
                        tableName = tableInfo.name,
                    )
                        ?: return@withContext
                }

                tableDao.insertTableItems(
                    tableInfo.items.mapIndexedNotNull { index, item ->
                        tableInfo.items.getOrNull(index)?.let { columnName ->
                            item.toEntity(
                                tableId = tableId,
                            )
                        }
                    },
                )
            }
        }
    }

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?> =
        tableDao.observeTable(deviceId = deviceIdAndPackageName.deviceId, packageName = deviceIdAndPackageName.packageName, tableId = tableId)
            .flatMapLatest { table ->
                if (table == null) {
                    flowOf(null)
                } else {
                    tableDao.observeTableItems(
                        tableId = table.id,
                    ).map { tableItem ->
                        TableDomainModel(
                            name = table.name,
                            items = tableItem.map {
                                TableDomainModel.TableItem(
                                    itemId = it.itemId,
                                    createdAt = it.createdAt,
                                    values = it.values,
                                    columns = it.columnsNames,
                                )
                            },
                        )
                    }
                }
                    .flowOn(dispatcherProvider.data)
            }

    override fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>> =
        tableDao.observeTablesForDevice(deviceIdAndPackageName.deviceId, packageName = deviceIdAndPackageName.packageName)
            .map { list -> list.map { toDomain(it) } }
            .flowOn(dispatcherProvider.data)

    override suspend fun delete(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel) {
        tableDao.deleteTableContent(tableId = tableId.id)
    }

    override suspend fun getDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<TableIdentifierDomainModel> =
        tableDao.getTablesForDevice(deviceId = deviceIdAndPackageName.deviceId, packageName = deviceIdAndPackageName.packageName).map {
            toDomain(it)
        }

    private fun toDomain(entity: TableEntity): TableIdentifierDomainModel = TableIdentifierDomainModel(
        id = entity.id,
        name = entity.name,
    )
}

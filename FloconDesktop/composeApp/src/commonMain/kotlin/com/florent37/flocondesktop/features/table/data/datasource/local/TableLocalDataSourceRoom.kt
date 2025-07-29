package com.florent37.flocondesktop.features.table.data.datasource.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.table.data.datasource.local.mapper.toEntity
import com.florent37.flocondesktop.features.table.data.datasource.local.model.TableEntity
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
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

    override suspend fun insert(deviceId: DeviceId, tablePartialInfos: List<TableDomainModel>) {
        withContext(dispatcherProvider.data) {
            tablePartialInfos.forEach { tableInfo ->
                var tableId = tableDao.insertTable(tableInfo.toEntity(deviceId))
                if (tableId == -1L) {
                    // table already exists
                    tableId =
                        tableDao.getTableId(deviceId = deviceId, tableName = tableInfo.name)
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

    override fun observe(deviceId: DeviceId, tableId: TableId): Flow<TableDomainModel?> = tableDao.observeTable(deviceId = deviceId, tableId = tableId)
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
        }.flowOn(dispatcherProvider.data)

    override fun observeDeviceTables(deviceId: DeviceId): Flow<List<TableIdentifierDomainModel>> = tableDao.observeTablesForDevice(deviceId).map { list ->
        list.map {
            toDomain(it)
        }
    }

    override suspend fun delete(deviceId: DeviceId, tableId: TableIdentifierDomainModel) {
        tableDao.deleteTableContent(tableId = tableId.id)
    }

    override suspend fun getDeviceTables(deviceId: DeviceId): List<TableIdentifierDomainModel> = tableDao.getTablesForDevice(deviceId).map {
        toDomain(it)
    }

    private fun toDomain(entity: TableEntity): TableIdentifierDomainModel = TableIdentifierDomainModel(
        id = entity.id,
        name = entity.name,
    )
}

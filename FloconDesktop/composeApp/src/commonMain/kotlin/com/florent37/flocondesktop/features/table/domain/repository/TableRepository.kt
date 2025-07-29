package com.florent37.flocondesktop.features.table.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun observeTable(deviceId: DeviceId, tableId: TableId): Flow<TableDomainModel?>
    suspend fun deleteTable(deviceId: DeviceId, tableId: TableIdentifierDomainModel)

    suspend fun selectDeviceTable(deviceId: DeviceId, tableId: TableId)
    fun observeSelectedDeviceTable(deviceId: DeviceId): Flow<TableIdentifierDomainModel?>
    fun observeDeviceTables(deviceId: DeviceId): Flow<List<TableIdentifierDomainModel>>
}

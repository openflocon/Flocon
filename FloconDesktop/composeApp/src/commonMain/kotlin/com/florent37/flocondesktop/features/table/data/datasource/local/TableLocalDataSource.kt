package com.florent37.flocondesktop.features.table.data.datasource.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.table.domain.model.TableDomainModel
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface TableLocalDataSource {
    suspend fun insert(deviceId: DeviceId, tablePartialInfos: List<TableDomainModel>)
    fun observe(deviceId: DeviceId, tableId: TableId): Flow<TableDomainModel?>
    fun observeDeviceTables(deviceId: DeviceId): Flow<List<TableIdentifierDomainModel>>
    suspend fun getDeviceTables(deviceId: DeviceId): List<TableIdentifierDomainModel>

    suspend fun delete(
        deviceId: DeviceId,
        tableId: TableIdentifierDomainModel,
    )
}

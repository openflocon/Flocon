package io.github.openflocon.flocondesktop.features.table.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
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

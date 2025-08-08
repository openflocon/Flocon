package io.github.openflocon.flocondesktop.features.table.domain.repository

import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageName, tableId: TableId): Flow<TableDomainModel?>
    suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageName, tableId: TableIdentifierDomainModel)

    suspend fun selectDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageName, tableId: TableId)
    fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<TableIdentifierDomainModel?>
    fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<TableIdentifierDomainModel>>
}

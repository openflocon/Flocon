package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceTablesDataSource {
    fun observeSelectedDeviceTable(deviceId: DeviceId): Flow<TableIdentifierDomainModel?>
    fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceId: DeviceId,
        tableId: TableId,
    )
}

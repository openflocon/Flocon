package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.table.domain.model.TableId
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceTablesDataSource {
    fun observeSelectedDeviceTable(deviceId: DeviceId): Flow<TableIdentifierDomainModel?>
    fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceId: DeviceId,
        tableId: TableId,
    )
}

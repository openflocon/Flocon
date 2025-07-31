package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceTablesDataSourceInMemory : DeviceTablesDataSource {
    private val selectedDeviceTables =
        MutableStateFlow<Map<DeviceId, TableIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceTable(deviceId: DeviceId): Flow<TableIdentifierDomainModel?> = selectedDeviceTables
        .map {
            it[deviceId]
        }.distinctUntilChanged()

    override fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceId: DeviceId,
        tableId: TableId,
    ) {
        val table = deviceTables.firstOrNull { it.id == tableId } ?: return

        selectedDeviceTables.update {
            it + (deviceId to table)
        }
    }
}

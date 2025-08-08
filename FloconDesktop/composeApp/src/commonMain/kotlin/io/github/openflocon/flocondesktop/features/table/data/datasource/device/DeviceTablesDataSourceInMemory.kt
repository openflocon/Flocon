package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceTablesDataSourceInMemory : DeviceTablesDataSource {
    private val selectedDeviceTables =
        MutableStateFlow<Map<DeviceIdAndPackageName, TableIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<TableIdentifierDomainModel?> = selectedDeviceTables
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectDeviceTable(deviceTables: List<TableIdentifierDomainModel>, deviceIdAndPackageName: DeviceIdAndPackageName, tableId: TableId) {
        val table = deviceTables.firstOrNull { it.id == tableId } ?: return

        selectedDeviceTables.update {
            it + (deviceIdAndPackageName to table)
        }
    }
}

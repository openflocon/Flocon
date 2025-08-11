package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.domain.models.TableId
import io.github.openflocon.domain.models.TableIdentifierDomainModel
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceTablesDataSourceInMemory : DeviceTablesDataSource {
    private val selectedDeviceTables =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, TableIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?> = selectedDeviceTables
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectDeviceTable(deviceTables: List<TableIdentifierDomainModel>, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId) {
        val table = deviceTables.firstOrNull { it.id == tableId } ?: return

        selectedDeviceTables.update {
            it + (deviceIdAndPackageName to table)
        }
    }
}

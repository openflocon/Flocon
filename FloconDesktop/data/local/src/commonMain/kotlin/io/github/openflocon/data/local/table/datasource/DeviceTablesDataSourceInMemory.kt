package io.github.openflocon.data.local.table.datasource

import io.github.openflocon.data.core.table.datasource.DeviceTablesDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class DeviceTablesDataSourceInMemory : DeviceTablesDataSource {
    private val selectedDeviceTables =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, TableIdentifierDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?> =
        selectedDeviceTables
            .map { it[deviceIdAndPackageName] }
            .distinctUntilChanged()

    override fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        tableId: TableId
    ) {
        val table = deviceTables.firstOrNull { it.id == tableId } ?: return

        selectedDeviceTables.update {
            it + (deviceIdAndPackageName to table)
        }
    }
}

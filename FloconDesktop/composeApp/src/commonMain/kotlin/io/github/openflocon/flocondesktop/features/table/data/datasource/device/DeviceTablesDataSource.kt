package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceTablesDataSource {
    fun observeSelectedDeviceTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<TableIdentifierDomainModel?>

    fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        tableId: TableId,
    )
}

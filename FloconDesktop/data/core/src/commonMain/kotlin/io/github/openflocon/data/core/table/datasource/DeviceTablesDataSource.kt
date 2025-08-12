package io.github.openflocon.data.core.table.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceTablesDataSource {
    fun observeSelectedDeviceTable(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<TableIdentifierDomainModel?>

    fun selectDeviceTable(
        deviceTables: List<TableIdentifierDomainModel>,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        tableId: TableId,
    )
}

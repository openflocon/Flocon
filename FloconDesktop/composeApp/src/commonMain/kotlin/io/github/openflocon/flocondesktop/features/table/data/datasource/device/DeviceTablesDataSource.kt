package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import io.github.openflocon.domain.models.TableId
import io.github.openflocon.domain.models.TableIdentifierDomainModel
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
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

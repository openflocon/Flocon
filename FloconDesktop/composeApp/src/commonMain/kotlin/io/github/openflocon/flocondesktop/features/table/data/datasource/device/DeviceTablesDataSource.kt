package io.github.openflocon.flocondesktop.features.table.data.datasource.device

import com.flocon.library.domain.models.TableId
import com.flocon.library.domain.models.TableIdentifierDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
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

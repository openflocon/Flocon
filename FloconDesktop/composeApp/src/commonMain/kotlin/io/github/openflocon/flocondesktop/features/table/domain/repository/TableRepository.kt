package io.github.openflocon.flocondesktop.features.table.domain.repository

import io.github.openflocon.domain.models.TableDomainModel
import io.github.openflocon.domain.models.TableId
import io.github.openflocon.domain.models.TableIdentifierDomainModel
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?>
    suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel)

    suspend fun selectDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId)
    fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?>
    fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>>
}

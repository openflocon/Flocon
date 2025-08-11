package io.github.openflocon.domain.table.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    fun observeTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?>
    suspend fun deleteTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableIdentifierDomainModel)

    suspend fun selectDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId)
    fun observeSelectedDeviceTable(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<TableIdentifierDomainModel?>
    fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>>
}

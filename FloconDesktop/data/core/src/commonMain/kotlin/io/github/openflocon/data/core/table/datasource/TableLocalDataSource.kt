package io.github.openflocon.data.core.table.datasource

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel
import io.github.openflocon.domain.table.models.TableId
import io.github.openflocon.domain.table.models.TableIdentifierDomainModel
import kotlinx.coroutines.flow.Flow

interface TableLocalDataSource {

    suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tablePartialInfos: List<TableDomainModel>)

    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, tableId: TableId): Flow<TableDomainModel?>

    fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<TableIdentifierDomainModel>>

    suspend fun getDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<TableIdentifierDomainModel>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        tableId: TableIdentifierDomainModel,
    )

    suspend fun deleteItem(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        itemId: String
    )
    suspend fun deleteBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        itemId: String
    )
}

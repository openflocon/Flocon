package io.github.openflocon.flocondesktop.features.table.data.datasource.local

import com.flocon.library.domain.models.TableDomainModel
import com.flocon.library.domain.models.TableId
import com.flocon.library.domain.models.TableIdentifierDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
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
}

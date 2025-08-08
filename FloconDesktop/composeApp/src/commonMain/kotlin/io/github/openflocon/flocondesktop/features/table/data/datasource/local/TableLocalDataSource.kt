package io.github.openflocon.flocondesktop.features.table.data.datasource.local

import io.github.openflocon.flocondesktop.features.table.domain.model.TableDomainModel
import io.github.openflocon.flocondesktop.features.table.domain.model.TableId
import io.github.openflocon.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import kotlinx.coroutines.flow.Flow

interface TableLocalDataSource {
    suspend fun insert(deviceIdAndPackageName: DeviceIdAndPackageName, tablePartialInfos: List<TableDomainModel>)
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageName, tableId: TableId): Flow<TableDomainModel?>
    fun observeDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<TableIdentifierDomainModel>>
    suspend fun getDeviceTables(deviceIdAndPackageName: DeviceIdAndPackageName): List<TableIdentifierDomainModel>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        tableId: TableIdentifierDomainModel,
    )
}

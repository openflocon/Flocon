package io.github.openflocon.data.core.table.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel

interface TableRemoteDataSource {
    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>)

    fun getItems(message: FloconIncomingMessageDomainModel): List<TableDomainModel>

}

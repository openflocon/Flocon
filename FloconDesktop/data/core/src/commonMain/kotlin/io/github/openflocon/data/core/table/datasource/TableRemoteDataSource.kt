package io.github.openflocon.data.core.table.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

interface TableRemoteDataSource {
    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>)
}

package io.github.openflocon.data.core.analytics.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

interface AnalyticsRemoteDataSource {
    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>)
}

package io.github.openflocon.data.core.analytics.datasource

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface AnalyticsRemoteDataSource {

    suspend fun clearReceivedItem(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, items: List<String>)

    fun getItems(message: FloconIncomingMessageDomainModel): List<AnalyticsItemDomainModel>

}

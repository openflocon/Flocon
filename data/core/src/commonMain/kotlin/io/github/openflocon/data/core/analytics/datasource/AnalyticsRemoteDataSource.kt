package io.github.openflocon.data.core.analytics.datasource

import io.github.openflocon.domain.analytics.models.AnalyticsItemDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface AnalyticsRemoteDataSource {

    fun getItems(message: FloconIncomingMessageDomainModel): List<AnalyticsItemDomainModel>
}

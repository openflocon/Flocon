package io.github.openflocon.data.core.deeplink.datasource

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface DeeplinkRemoteDataSource {

    fun getItems(message: FloconIncomingMessageDomainModel): List<DeeplinkDomainModel>
}

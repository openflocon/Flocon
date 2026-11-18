package io.github.openflocon.data.core.deeplink.datasource

import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface DeeplinkRemoteDataSource {

    fun getItems(message: FloconIncomingMessageDomainModel): Deeplinks?
}

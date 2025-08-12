package com.flocon.data.remote.deeplink.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.deeplink.models.DeeplinksReceivedDataModel
import com.flocon.data.remote.deeplink.models.toDomain
import io.github.openflocon.data.core.deeplink.datasource.DeeplinkRemoteDataSource
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

internal class DeeplinkRemoteDataSourceImpl(
    private val json: Json
) : DeeplinkRemoteDataSource {

    override fun getItems(message: FloconIncomingMessageDomainModel): List<DeeplinkDomainModel> {
        return json.safeDecodeFromString<DeeplinksReceivedDataModel>(message.body)
            ?.toDomain()
            .orEmpty()
    }

}

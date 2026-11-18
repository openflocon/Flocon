package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

interface NetworkReplayDataSource {
    suspend fun replay(request: FloconNetworkCallDomainModel): FloconNetworkCallDomainModel
}

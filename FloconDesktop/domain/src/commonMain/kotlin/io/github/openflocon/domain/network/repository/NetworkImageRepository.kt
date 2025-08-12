package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        call: FloconNetworkCallDomainModel,
    )
}

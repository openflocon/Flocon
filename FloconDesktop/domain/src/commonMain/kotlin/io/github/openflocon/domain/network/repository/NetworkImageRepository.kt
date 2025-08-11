package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    )
}

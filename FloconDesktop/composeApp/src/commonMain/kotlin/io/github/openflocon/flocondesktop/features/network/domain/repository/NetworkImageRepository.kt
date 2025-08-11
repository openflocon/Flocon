package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.domain.models.FloconHttpRequestDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    )
}

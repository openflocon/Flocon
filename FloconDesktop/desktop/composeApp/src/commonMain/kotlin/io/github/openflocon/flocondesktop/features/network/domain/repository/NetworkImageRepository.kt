package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    )
}

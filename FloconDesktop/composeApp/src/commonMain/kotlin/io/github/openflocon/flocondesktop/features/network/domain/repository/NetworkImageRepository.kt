package io.github.openflocon.flocondesktop.features.network.domain.repository

import com.flocon.library.domain.models.FloconHttpRequestDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    )
}

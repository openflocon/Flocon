package com.florent37.flocondesktop.features.network.domain.repository

import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel

interface NetworkImageRepository {
    suspend fun onImageReceived(
        deviceId: String,
        request: FloconHttpRequestDomainModel,
    )
}

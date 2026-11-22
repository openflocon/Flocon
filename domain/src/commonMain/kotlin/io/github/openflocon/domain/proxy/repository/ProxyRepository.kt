package io.github.openflocon.domain.proxy.repository

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import kotlinx.coroutines.flow.Flow

interface ProxyRepository {
    val state: Flow<ProxyStateDomainModel>

    /** The choice of the user, kept across launches. */
    val isEnabled: Flow<Boolean>

    suspend fun setEnabled(enabled: Boolean)

    /** Starts the proxy (restarts it if it runs), connected to the Flocon websocket on [websocketPort]. */
    suspend fun start(websocketPort: Int)

    suspend fun stop()
}

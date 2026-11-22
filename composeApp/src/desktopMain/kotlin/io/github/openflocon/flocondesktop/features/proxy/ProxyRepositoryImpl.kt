package io.github.openflocon.flocondesktop.features.proxy

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.domain.proxy.repository.ProxyRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow

internal class ProxyRepositoryImpl(
    private val proxyManager: ProxyManager,
    private val settingsDataSource: SettingsDataSource,
) : ProxyRepository {

    override val state: Flow<ProxyStateDomainModel> = proxyManager.state

    override val isEnabled: Flow<Boolean> = settingsDataSource.proxyEnabled

    override suspend fun setEnabled(enabled: Boolean) {
        settingsDataSource.setProxyEnabled(enabled)
    }

    // the desktop websocket is TLS, with a self-signed certificate the proxy accepts
    override suspend fun start(websocketPort: Int) {
        proxyManager.start(floconWebsocketUrl = "wss://127.0.0.1:$websocketPort/")
    }

    override suspend fun stop() {
        proxyManager.stop()
    }
}

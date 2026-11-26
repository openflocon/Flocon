package io.github.openflocon.flocondesktop.features.proxy

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.domain.proxy.repository.ProxyRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import java.util.Locale

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
    override suspend fun start(websocketPort: Int, languageTag: String?) {
        proxyManager.start(
            floconWebsocketUrl = "wss://127.0.0.1:$websocketPort/",
            // the language asked to Flocon, else the one of the computer: the certificate page follows it
            locale = languageTag ?: Locale.getDefault().toLanguageTag(),
        )
    }

    override suspend fun stop() {
        proxyManager.stop()
    }
}

package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class FloconDeeplinksPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconDeeplinksPlugin {

    private val deeplinks = MutableStateFlow<List<DeeplinkModel>?>(null)
    private val variables = MutableStateFlow<List<DeeplinkVariable>?>(null)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {

    }

    override fun onConnectedToServer() {
        // on connected, send known dashboard
        deeplinks.value?.let {
            registerDeeplinks(it, variables.value.orEmpty())
        }
    }

    override fun registerDeeplinks(
        deeplinks: List<DeeplinkModel>,
        variables: List<DeeplinkVariable>
    ) {
        this.deeplinks.update { deeplinks }
        this.variables.update { variables }

        try {
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = toDeeplinksJson(
                    deeplinks = deeplinks,
                    variables = variables
                )
            )
        } catch (t: Throwable) {
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}


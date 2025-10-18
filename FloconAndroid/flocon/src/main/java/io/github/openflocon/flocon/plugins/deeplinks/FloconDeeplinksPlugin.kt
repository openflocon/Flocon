package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel

internal class FloconDeeplinksPluginImpl(
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconDeeplinksPlugin {

    private val deeplinks = java.util.concurrent.atomic.AtomicReference<List<DeeplinkModel>?>(null)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {

    }

    override fun onConnectedToServer() {
        // on connected, send known dashboard
        deeplinks.get()?.let {
            registerDeeplinks(it)
        }
    }

    override fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        this.deeplinks.set(deeplinks)

        try {
            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = toDeeplinksJson(deeplinks)
            )
        } catch (t: Throwable) {
            FloconLogger.logError("deeplink mapping error", t)
        }
    }
}


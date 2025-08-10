package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.deeplinks.model.Deeplink
import org.json.JSONArray
import org.json.JSONObject

class FloconDeeplinksPluginImpl(
    private val sender: FloconMessageSender,
) : FloconDeeplinksPlugin {

    private val deeplinks = java.util.concurrent.atomic.AtomicReference<List<Deeplink>?>(null)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {

    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // on connected, send known dashboard
        deeplinks.get()?.let {
            registerDeeplinks(it)
        }
    }

    override fun registerDeeplinks(deeplinks: List<Deeplink>) {
        val dashboardJson = toDeeplinksJson(deeplinks)

        this.deeplinks.set(deeplinks)

        sender.send(
            plugin = Protocol.FromDevice.Deeplink.Plugin,
            method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
            body = dashboardJson.toString()
        )
    }

    private fun toDeeplinksJson(deeplinks: List<Deeplink>): JSONObject {
        val jsonObject = JSONObject()
        val array = JSONArray()
        deeplinks.forEach { deeplink ->
            array.put(JSONObject().apply {
                deeplink.label?.let {
                    put("label", it)
                }
                put("link", deeplink.link)
                deeplink.description?.let {
                    put("description", it)
                }
            })
        }
        jsonObject.put("deeplinks", array)
        return jsonObject
    }
}


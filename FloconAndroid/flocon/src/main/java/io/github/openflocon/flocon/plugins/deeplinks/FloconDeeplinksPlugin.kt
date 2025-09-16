package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkModel
import org.json.JSONArray
import org.json.JSONObject

internal class FloconDeeplinksPluginImpl(
    private val sender: FloconMessageSender,
) : FloconDeeplinksPlugin {

    private val deeplinks = java.util.concurrent.atomic.AtomicReference<List<DeeplinkModel>?>(null)

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

    override fun registerDeeplinks(deeplinks: List<DeeplinkModel>) {
        this.deeplinks.set(deeplinks)

        try {
            val deeplinksJson = toDeeplinksJson(deeplinks)

            sender.send(
                plugin = Protocol.FromDevice.Deeplink.Plugin,
                method = Protocol.FromDevice.Deeplink.Method.GetDeeplinks,
                body = deeplinksJson.toString()
            )
        } catch (t: Throwable) {
            FloconLogger.logError("deeplink mapping error", t)
        }
    }

    private fun toDeeplinksJson(deeplinks: List<DeeplinkModel>): JSONObject {
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

                val parametersArray = JSONArray()
                deeplink.parameters.forEach { param ->
                    parametersArray.put(JSONObject().apply {
                        put("paramName", param.paramName)
                        val autoCompleteArray = JSONArray()
                        param.autoComplete.forEach { value ->
                            autoCompleteArray.put(value)
                        }
                        put("autoComplete", autoCompleteArray)
                    })
                }
                put("parameters", parametersArray)
            })
        }
        jsonObject.put("deeplinks", array)
        return jsonObject
    }
}


package io.github.openflocon.flocondesktop.features.proxy

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** One line printed by flocon-proxy on stdout. */
internal sealed interface ProxyEvent {
    data class Ready(val proxyPort: Int, val caPort: Int, val ips: List<String>) : ProxyEvent
    data class Error(val message: String) : ProxyEvent
    data class TlsError(val host: String?, val reason: String?) : ProxyEvent
}

internal fun parseProxyEvent(line: String): ProxyEvent? {
    val obj = runCatching { Json.parseToJsonElement(line).jsonObject }.getOrNull() ?: return null
    return when (obj.string("event")) {
        "ready" -> {
            val proxyPort = obj["proxyPort"]?.jsonPrimitive?.intOrNull ?: return null
            val caPort = obj["caPort"]?.jsonPrimitive?.intOrNull ?: return null
            val ips = runCatching { obj["ips"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull } }
                .getOrNull()
                .orEmpty()
            ProxyEvent.Ready(proxyPort, caPort, ips)
        }

        "error" -> ProxyEvent.Error(obj.string("message") ?: "Unknown error")

        "tls-error" -> ProxyEvent.TlsError(obj.string("host"), obj.string("reason"))

        else -> null
    }
}

private fun JsonObject.string(key: String): String? = runCatching { this[key]?.jsonPrimitive?.contentOrNull }.getOrNull()

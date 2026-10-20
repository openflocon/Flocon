package io.github.openflocon.flocondesktop.telemetry

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/** Sends a single event. Implementations must never throw: telemetry can't break the app. */
interface TelemetryClient {
    suspend fun capture(
        event: String,
        distinctId: String,
        properties: Map<String, Any>,
    )
}

/** Used when the build has no API key (local builds, forks). */
internal object NoOpTelemetryClient : TelemetryClient {
    override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) = Unit
}

/** Minimal client for PostHog's capture API, no SDK needed. */
internal class PostHogTelemetryClient(
    private val config: TelemetryConfig,
) : TelemetryClient {

    private val client by lazy {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
            }
        }
    }

    override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) {
        val body = buildJsonObject {
            put("api_key", config.apiKey)
            put("event", event)
            put("distinct_id", distinctId)
            put(
                "properties",
                buildJsonObject {
                    properties.forEach { (key, value) ->
                        put(
                            key,
                            when (value) {
                                is Boolean -> JsonPrimitive(value)
                                is Number -> JsonPrimitive(value)
                                else -> JsonPrimitive(value.toString())
                            },
                        )
                    }
                },
            )
        }

        try {
            val response = client.post("${config.host.trimEnd('/')}/i/v0/e/") {
                contentType(ContentType.Application.Json)
                setBody(body.toString())
            }
            if (!response.status.isSuccess()) {
                Logger.w(TAG) { "Telemetry event '$event' rejected: ${response.status}" }
            }
        } catch (e: Exception) {
            // Offline, proxy, firewall... never surface it to the user.
            Logger.d(TAG) { "Telemetry event '$event' not sent: ${e.message}" }
        }
    }

    private companion object {
        const val TAG = "Telemetry"
    }
}

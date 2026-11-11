package com.flocon.data.remote.telemetry.datasource

import co.touchlab.kermit.Logger
import io.github.openflocon.data.core.telemetry.datasource.TelemetryRemoteDataSource
import io.github.openflocon.domain.telemetry.models.TelemetryConfigDomainModel
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

/** Minimal client for PostHog's capture API, no SDK needed. */
class PostHogTelemetryRemoteDataSource : TelemetryRemoteDataSource {

    private val client by lazy {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
            }
        }
    }

    override suspend fun capture(
        config: TelemetryConfigDomainModel,
        event: String,
        distinctId: String,
        properties: Map<String, Any>,
    ) {
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

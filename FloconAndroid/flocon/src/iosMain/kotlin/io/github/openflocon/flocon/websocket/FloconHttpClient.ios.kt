package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal actual fun buildFloconHttpClient(): FloconHttpClient {
    return FloconHttpClientIOs()
}

@OptIn(io.github.openflocon.flocon.dsl.FloconMarker::class)
internal class FloconHttpClientIOs() : FloconHttpClient {

    // client configurable selon la plateforme (Android, iOS, JVM, etc.)
    private val client = HttpClient(Darwin.create()) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        defaultRequest {
            header(HttpHeaders.UserAgent, "Flocon")
        }
    }

    override suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
        address: String,
        port: Int,
        deviceId: String,
        appPackageName: String,
        appInstance: Long
    ): Boolean {
        // not supported on iOS yet: the files plugin has no iOS data source, so no file is ever sent
        return false
    }
}
package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.FloconLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.isSuccess
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.Json
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun readBytes(path: String): ByteArray? {
    val data = NSData.dataWithContentsOfFile(path) ?: return null
    val size = data.length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).also { bytes ->
        bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
    }
}

internal actual fun buildFloconHttpClient(): FloconHttpClient {
    return FloconHttpClientIOs()
}

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
        val uploadUrl = "http://$address:$port/upload"

        return try {
            val fileBytes = readBytes(file.path) ?: return false
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = uploadUrl,
                formData = formData {
                    append("remotePath", infos.path)
                    append("requestId", infos.requestId)
                    append("deviceId", deviceId)
                    append("appPackageName", appPackageName)
                    append("appInstance", appInstance.toString())
                    append(
                        key = "file",
                        value = fileBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.path.substringAfterLast('/')}\"")
                            append(HttpHeaders.ContentType, "application/octet-stream")
                        }
                    )
                }
            )
            response.status.isSuccess()
        } catch (e: Exception) {
            FloconLogger.logError("Upload failed : ${e.message}", e)
            false
        }
    }
}
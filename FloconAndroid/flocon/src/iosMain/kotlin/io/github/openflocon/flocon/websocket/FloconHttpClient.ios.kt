package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

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
        /* no op for now on ios
        val uploadUrl = "http://$address:$port/upload"

        try {
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = uploadUrl,
                formData = formData {
                    append("remotePath", infos.path)
                    append("requestId", infos.requestId)
                    append("deviceId", deviceId)
                    append("appPackageName", appPackageName)
                    append("appInstance", appInstance.toString())

                    // Ajout du fichier binaire
                    val fileBytes = file.file.readBytes()
                    append(
                        key = "file",
                        value = fileBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.file.name}\"")
                            append(HttpHeaders.ContentType, "application/octet-stream")
                        }
                    )
                }
            )

            if (response.status.isSuccess()) {
                println("✅ Upload réussi : ${response.bodyAsText()}")
                return true
            } else {
                println("❌ Erreur serveur (${response.status}): ${response.bodyAsText()}")
                return false
            }
        } catch (e: Exception) {
            println("❌ Erreur lors de l’envoi : ${e.message}")
            return false
        }
         */
        return false
    }
}
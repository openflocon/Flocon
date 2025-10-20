package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

internal class FloconHttpClientImpl : FloconHttpClient {

    private val client by lazy {
        OkHttpClient()
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

        val file = file.file
        // Corps du fichier (binaire)
        val fileBody = file.asRequestBody("application/octet-stream".toMediaType())

        // Construction du multipart
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart("remotePath", infos.path)
            .addFormDataPart("requestId", infos.requestId)

            .addFormDataPart("deviceId", deviceId)
            .addFormDataPart("appPackageName", appPackageName)
            .addFormDataPart("appInstance", appInstance.toString())

            .addFormDataPart("file", file.name, fileBody)
            .build()

        val request = Request.Builder()
            .url(uploadUrl)
            .post(multipartBody)
            .build()

        val response = client.newCall(request).execute()
        response.use {
            if (it.isSuccessful) {
                println("✅ Upload réussi : ${it.body?.string()}")
                return true
            } else {
                println("❌ Erreur serveur (${it.code}): ${it.body?.string()}")
                return false
            }
        }
    }
}
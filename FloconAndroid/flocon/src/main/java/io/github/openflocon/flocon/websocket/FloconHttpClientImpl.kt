package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.model.FloconFileInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class FloconHttpClientImpl : FloconHttpClient {

    private val client by lazy {
        OkHttpClient()
    }

    override suspend fun send(
        file: File,
        infos: FloconFileInfo,
        address: String,
        port: Int
    ): Boolean {
        val uploadUrl = "http://$address:$port/upload"

        // Corps du fichier (binaire)
        val fileBody = file.asRequestBody("application/octet-stream".toMediaType())

        // Construction du multipart
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart("filePath", infos.path)
            .addFormDataPart("requestId", infos.requestId)

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
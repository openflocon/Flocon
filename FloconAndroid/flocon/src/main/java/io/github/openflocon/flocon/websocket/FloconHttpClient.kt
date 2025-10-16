package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.model.FloconFileInfo
import java.io.File

interface FloconHttpClient {
    suspend fun send(file: File, infos: FloconFileInfo, address: String, port: Int) : Boolean
}
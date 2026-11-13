package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo

internal expect fun buildFloconHttpClient(): FloconHttpClient

internal interface FloconHttpClient {

    suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
        address: String,
        port: Int,
        deviceId: String,
        appPackageName: String,
        appInstance: Long
    ): Boolean

    /**
     * Sends an already encoded message (the one the websocket would carry) to `POST /message`.
     * Used for the messages too big for a websocket frame, see [FloconClient].
     */
    suspend fun sendMessage(
        message: String,
        address: String,
        port: Int,
    ): Boolean
}
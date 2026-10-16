package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.model.FloconFileInfo

internal expect fun buildFloconHttpClient(): FloconHttpClient

internal interface FloconHttpClient {

    @FloconMarker
    suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
        address: String,
        port: Int,
        deviceId: String,
        appPackageName: String,
        appInstance: Long
    ): Boolean
}
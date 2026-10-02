package io.github.openflocon.flocon.websocket

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.model.FloconFileInfo

internal actual fun buildFloconHttpClient(): FloconHttpClient = object : FloconHttpClient {
    @FloconMarker
    override suspend fun send(
        file: FloconFile,
        infos: FloconFileInfo,
        address: String,
        port: Int,
        deviceId: String,
        appPackageName: String,
        appInstance: Long
    ): Boolean = false
}

package io.github.openflocon.domain.models.settings

import io.github.openflocon.domain.Constant

data class ServerPortsDomainModel(
    val websocketPort: Int = Constant.SERVER_WEBSOCKET_PORT,
    val httpPort: Int = Constant.SERVER_HTTP_PORT,
) {
    val isDefault: Boolean
        get() = websocketPort == Constant.SERVER_WEBSOCKET_PORT && httpPort == Constant.SERVER_HTTP_PORT
}

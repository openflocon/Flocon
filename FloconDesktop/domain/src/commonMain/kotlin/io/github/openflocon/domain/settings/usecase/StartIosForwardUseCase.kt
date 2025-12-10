package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.Constant

interface IosExecutor {
    fun startForward(localPort: Int, devicePort: Int, onOutput: (String) -> Unit)
    fun stopForward(localPort: Int)
}

class StartIosForwardUseCase(
    private val iosExecutor: IosExecutor
) {
    // We launch it for WebSocket and HTTP ports
    operator fun invoke() {
        // Forward Desktop:9023 -> Device:9023
        iosExecutor.startForward(
            localPort = Constant.SERVER_WEBSOCKET_PORT, 
            devicePort = Constant.SERVER_WEBSOCKET_PORT,
            onOutput = {}
        )
        // Forward Desktop:9024 -> Device:9024
        iosExecutor.startForward(
            localPort = Constant.SERVER_HTTP_PORT, 
            devicePort = Constant.SERVER_HTTP_PORT,
            onOutput = {}
        )
    }
}

package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconContext

internal actual fun getAppInfos(floconContext: FloconContext): AppInfos = AppInfos(
    deviceId = "wasm-device",
    deviceName = "Browser",
    appName = "Flocon Wasm",
    appPackageName = "io.github.openflocon.wasm",
    platform = "wasm"
)

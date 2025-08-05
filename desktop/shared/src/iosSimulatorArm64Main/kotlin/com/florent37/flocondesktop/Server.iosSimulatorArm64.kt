package io.github.openflocon.flocondesktop

actual fun getServer(): io.github.openflocon.flocondesktop.Server {
    return ServerNoOp()
}

package com.florent37.flocondesktop

actual fun getServer(): com.florent37.flocondesktop.Server {
    return ServerNoOp()
}
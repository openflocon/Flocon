package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.InternalFloconApi

// no Brotli decoder on this platform
@InternalFloconApi
actual fun decodeBrotli(bytes: ByteArray): ByteArray? = null

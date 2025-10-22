package io.github.openflocon.flocon.core

import kotlinx.serialization.json.Json

internal object FloconEncoder {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
}
package io.github.openflocon.flocon.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object FloconEncoder {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false

        serializersModule = SerializersModule {

        }
    }
}
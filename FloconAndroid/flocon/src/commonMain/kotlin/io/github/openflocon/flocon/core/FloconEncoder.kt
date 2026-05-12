package io.github.openflocon.flocon.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

class FloconEncoder internal constructor(
    val module: SerializersModule
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        serializersModule = module
    }

    fun <T> encode(serializer: KSerializer<T>, body: T): String = json.encodeToString(
        serializer = serializer,
        value = body
    )

    fun <T> decode(serializer: KSerializer<T>, body: String): T = json.decodeFromString(
        deserializer = serializer,
        string = body
    )

}

inline fun <reified T> FloconEncoder.encode(body: T) = encode(
    serializer = module.serializer<T>(),
    body = body
)

inline fun <reified T> FloconEncoder.decode(body: String) = decode(
    serializer = module.serializer<T>(),
    body = body
)
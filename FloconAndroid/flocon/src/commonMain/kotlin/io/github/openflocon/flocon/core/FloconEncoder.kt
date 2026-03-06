package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.plugins.deeplinks.model.DeeplinkParameterRemote
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal object FloconEncoder {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false

        serializersModule = SerializersModule {
            polymorphic(DeeplinkParameterRemote::class) {
                subclass(DeeplinkParameterRemote.AutoComplete::class)
                subclass(DeeplinkParameterRemote.Variable::class)
            }
        }
    }
}
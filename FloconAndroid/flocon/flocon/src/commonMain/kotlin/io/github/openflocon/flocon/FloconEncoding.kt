package io.github.openflocon.flocon

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface FloconEncoding {

    val serializersModule: SerializersModule

}

internal class DefaultEncoding : FloconEncoding {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()
}
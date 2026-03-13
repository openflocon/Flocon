package io.github.openflocon.flocon

import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@FloconMarker
interface FloconEncoding {

    val serializersModule: SerializersModule

}

@FloconMarker
internal class DefaultEncoding() : FloconEncoding {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()
}
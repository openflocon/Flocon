package io.github.openflocon.flocon

<<<<<<< HEAD
import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@FloconMarker
=======
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

>>>>>>> 2.0.0
interface FloconEncoding {

    val serializersModule: SerializersModule

}

<<<<<<< HEAD
@FloconMarker
internal class DefaultEncoding() : FloconEncoding {
=======
internal class DefaultEncoding : FloconEncoding {
>>>>>>> 2.0.0
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()
}
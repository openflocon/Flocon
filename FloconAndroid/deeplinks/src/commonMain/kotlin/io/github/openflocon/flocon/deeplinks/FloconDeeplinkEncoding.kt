package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.FloconEncoding
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.deeplinks.model.DeeplinkParameterRemote
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@FloconMarker
internal class FloconDeeplinkEncoding : FloconEncoding {
    override val serializersModule: SerializersModule = SerializersModule {
        polymorphic(DeeplinkParameterRemote::class) {
            subclass(DeeplinkParameterRemote.AutoComplete::class)
            subclass(DeeplinkParameterRemote.Variable::class)
        }
    }
}
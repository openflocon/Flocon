package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconEncoding
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@FloconMarker
internal class FloconDatabaseEncoding : FloconEncoding {
    override val serializersModule: SerializersModule
        get() = SerializersModule {
            polymorphic(DatabaseExecuteResponse::class) {
                polymorphic(DatabaseExecuteSqlResponse::class) {
                    subclass(DatabaseExecuteSqlResponse.Insert::class)
                    subclass(DatabaseExecuteSqlResponse.UpdateDelete::class)
                    subclass(DatabaseExecuteSqlResponse.Select::class)
                    subclass(DatabaseExecuteSqlResponse.RawSuccess::class)
                    subclass(DatabaseExecuteSqlResponse.Error::class)

                    defaultDeserializer { DatabaseExecuteSqlResponse.Error.serializer() }
                }
            }
        }
}
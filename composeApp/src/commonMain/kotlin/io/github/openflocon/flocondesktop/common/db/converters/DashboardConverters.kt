package io.github.openflocon.flocondesktop.common.db.converters

import androidx.room.TypeConverter
import io.github.openflocon.data.local.dashboard.models.ContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.FormContainerConfigEntity
import io.github.openflocon.data.local.dashboard.models.SectionContainerConfigEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class DashboardConverters {

    @TypeConverter
    fun fromContainerConfig(value: ContainerConfigEntity): String = json.encodeToString(value)

    @TypeConverter
    fun toContainerConfig(value: String): ContainerConfigEntity = json.decodeFromString(value)

    private companion object {
        val json = Json {
            serializersModule = SerializersModule {
                polymorphic(ContainerConfigEntity::class) {
                    subclass(
                        FormContainerConfigEntity::class,
                        FormContainerConfigEntity.serializer()
                    )
                    subclass(
                        SectionContainerConfigEntity::class,
                        SectionContainerConfigEntity.serializer()
                    )
                }
            }
        }
    }
}

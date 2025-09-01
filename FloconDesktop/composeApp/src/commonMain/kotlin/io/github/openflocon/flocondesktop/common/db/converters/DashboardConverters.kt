package io.github.openflocon.flocondesktop.common.db.converters

import androidx.room.TypeConverter
import io.github.openflocon.domain.dashboard.models.ContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.FormContainerConfigDomainModel
import io.github.openflocon.domain.dashboard.models.SectionContainerConfigDomainModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.polymorphic

class DashboardConverters {

    @TypeConverter
    fun fromContainerConfig(value: ContainerConfigDomainModel): String =
        json.encodeToString(value)

    @TypeConverter
    fun toContainerConfig(value: String): ContainerConfigDomainModel =
        json.decodeFromString<ContainerConfigDomainModel>(value)

    private companion object {
        val json = Json {
            serializersModule = SerializersModule {
                polymorphic(ContainerConfigDomainModel::class) {
                    subclass(
                        FormContainerConfigDomainModel::class,
                        FormContainerConfigDomainModel.serializer()
                    )
                    subclass(
                        SectionContainerConfigDomainModel::class,
                        SectionContainerConfigDomainModel.serializer()
                    )
                }
            }
        }
    }
}

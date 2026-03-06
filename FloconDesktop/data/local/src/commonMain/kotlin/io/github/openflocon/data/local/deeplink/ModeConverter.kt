package io.github.openflocon.data.local.deeplink

import androidx.room.TypeConverter
import io.github.openflocon.data.local.JSON
import io.github.openflocon.data.local.deeplink.models.DeeplinkVariableEntity
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ModeConverter : KoinComponent {

    private val json by inject<Json>(JSON)

    @TypeConverter
    fun fromContainerConfig(value: DeeplinkVariableEntity.Mode): String = json.encodeToString(value)

    @TypeConverter
    fun toContainerConfig(value: String): DeeplinkVariableEntity.Mode = json.decodeFromString(value)

}

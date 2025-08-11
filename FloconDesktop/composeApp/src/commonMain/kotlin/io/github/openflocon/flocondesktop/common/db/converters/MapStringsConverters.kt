package io.github.openflocon.flocondesktop.common.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class MapStringsConverters {
    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String>? = value?.let { Json.decodeFromString<Map<String, String>>(it) }

    @TypeConverter
    fun toStringMap(map: Map<String, String>?): String? = map?.let { Json.encodeToString(it) }
}

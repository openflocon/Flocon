package io.github.openflocon.flocondesktop.common.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class ListStringsConverters {
    @TypeConverter
    fun fromStringList(value: String?): List<String>? = value?.let { Json.decodeFromString<List<String>>(it) }

    @TypeConverter
    fun toStringList(list: List<String>?): String? = list?.let { Json.encodeToString(it) }
}

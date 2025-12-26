package io.github.openflocon.flocon.plugins.database.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class DatabaseQueryLogModel(
    val dbName: String,
    val path: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString<DatabaseQueryLogModel>(this)
    }

    companion object {
        fun fromJson(json: String): DatabaseQueryLogModel {
            return FloconEncoder.json.decodeFromString<DatabaseQueryLogModel>(json)
        }
    }
}

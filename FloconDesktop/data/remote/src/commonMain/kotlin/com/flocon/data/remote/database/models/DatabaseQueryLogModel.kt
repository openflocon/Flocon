package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQueryLogModel(
    val dbName: String,
    val path: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
) {
    companion object {
        fun fromJson(json: String): DatabaseQueryLogModel? {
            return try {
                io.github.openflocon.domain.common.FloconJson.json.decodeFromString<DatabaseQueryLogModel>(json)
            } catch (t: Throwable) {
                null
            }
        }
    }
}

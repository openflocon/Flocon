package io.github.openflocon.flocon.crashreporter.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class CrashReportDataModel(
    val crashId: String,
    val timestamp: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)

fun CrashReportDataModel.toJson(): String {
    return Json.encodeToString(this)
}

fun crashReportFromJson(json: String): CrashReportDataModel {
    return Json.decodeFromString(json)
}
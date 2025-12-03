package io.github.openflocon.flocon.plugins.crashreporter.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun CrashReportDataModel.toJson(): String {
    return json.encodeToString(this)
}

fun crashReportFromJson(jsonString: String): CrashReportDataModel? {
    return try {
        json.decodeFromString<CrashReportDataModel>(jsonString)
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

fun crashReportsListToJson(crashes: List<CrashReportDataModel>): String {
    return json.encodeToString(crashes)
}

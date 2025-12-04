package io.github.openflocon.flocon.plugins.crashreporter.model

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun CrashReportDataModel.toJson(): String {
    return FloconEncoder.json.encodeToString(this)
}

internal fun crashReportFromJson(jsonString: String): CrashReportDataModel? {
    return try {
        FloconEncoder.json.decodeFromString<CrashReportDataModel>(jsonString)
    } catch (t: Throwable) {
        FloconLogger.logError("Crash report parsing error", t)
        null
    }
}

internal fun crashReportsListToJson(crashes: List<CrashReportDataModel>): String {
    return FloconEncoder.json.encodeToString(crashes)
}

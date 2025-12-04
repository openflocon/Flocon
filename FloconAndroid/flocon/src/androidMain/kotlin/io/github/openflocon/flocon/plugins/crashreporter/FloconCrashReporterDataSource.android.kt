package io.github.openflocon.flocon.plugins.crashreporter

import android.content.Context
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel
import io.github.openflocon.flocon.plugins.crashreporter.model.crashReportFromJson
import io.github.openflocon.flocon.plugins.crashreporter.model.toJson
import java.io.File

internal class FloconCrashReporterDataSourceAndroid(
    private val context: Context
) : FloconCrashReporterDataSource {

    private val crashesDir = File(context.filesDir, "flocon_crashes")

    init {
        crashesDir.mkdirs()
    }

    override fun saveCrash(crash: CrashReportDataModel) {
        try {
            val file = File(crashesDir, "${crash.crashId}.json")
            val jsonString = crash.toJson()
            file.writeText(jsonString)
        } catch (t: Throwable) {
            FloconLogger.logError("Error saving crash", t)
        }
    }

    override fun loadPendingCrashes(): List<CrashReportDataModel> {
        return try {
            crashesDir.listFiles()
                ?.mapNotNull { file ->
                    try {
                        crashReportFromJson(file.readText())
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        null
                    }
                } ?: emptyList()
        } catch (t: Throwable) {
            FloconLogger.logError("Error loading pending crashes", t)
            emptyList()
        }
    }

    override fun deleteCrash(crashId: String) {
        try {
            File(crashesDir, "$crashId.json").delete()
        } catch (t: Throwable) {
            FloconLogger.logError("Failed to delete crash report: $crashId.json", t)
        }
    }
}

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource {
    return FloconCrashReporterDataSourceAndroid(context.appContext)
}

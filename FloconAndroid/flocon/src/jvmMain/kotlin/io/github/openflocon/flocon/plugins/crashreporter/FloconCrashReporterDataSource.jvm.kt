package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel
import io.github.openflocon.flocon.plugins.crashreporter.model.crashReportFromJson
import io.github.openflocon.flocon.plugins.crashreporter.model.toJson
import java.io.File

internal class FloconCrashReporterDataSourceJvm : FloconCrashReporterDataSource {
    
    private val crashesDir: File by lazy {
        val userHome = System.getProperty("user.home") ?: System.getProperty("java.io.tmpdir") ?: "."
        val floconDir = File(userHome, ".flocon")
        val crashDir = File(floconDir, "crashes")
        crashDir.mkdirs()
        crashDir
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
                ?.filter { it.extension == "json" }
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
            FloconLogger.logError("Error deleting crash", t)
        }
    }
}

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource {
    return FloconCrashReporterDataSourceJvm()
}

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        try {
            // Save crash
            onCrash(throwable)
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            // Call original handler to let the app crash normally
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
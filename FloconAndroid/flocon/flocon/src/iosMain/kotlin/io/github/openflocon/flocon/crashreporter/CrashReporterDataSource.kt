@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package io.github.openflocon.flocon.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.crashreporter.model.CrashReportDataModel
import io.github.openflocon.flocon.crashreporter.model.crashReportFromJson
import io.github.openflocon.flocon.crashreporter.model.toJson
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.setUnhandledExceptionHook
import kotlin.native.terminateWithUnhandledException

private class FloconCrashReporterDataSourceIos : FloconCrashReporterDataSource {

    private val fileManager = NSFileManager.defaultManager

    private val crashesDir: String by lazy {
        val base = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: "."
        val dir = "$base/flocon/crashes"
        fileManager.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
        dir
    }

    override fun saveCrash(crash: CrashReportDataModel) {
        try {
            NSString.create(string = crash.toJson()).writeToFile(
                path = "$crashesDir/${crash.crashId}.json",
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null,
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Error saving crash", t)
        }
    }

    override fun loadPendingCrashes(): List<CrashReportDataModel> {
        return try {
            fileManager.contentsOfDirectoryAtPath(crashesDir, error = null)
                ?.mapNotNull { it as? String }
                ?.filter { it.endsWith(".json") }
                ?.mapNotNull { name ->
                    try {
                        NSString.stringWithContentsOfFile("$crashesDir/$name", NSUTF8StringEncoding, null)
                            ?.let { crashReportFromJson(it) }
                    } catch (t: Throwable) {
                        FloconLogger.logError("Error reading crash $name", t)
                        null
                    }
                }
                ?: emptyList()
        } catch (t: Throwable) {
            FloconLogger.logError("Error loading pending crashes", t)
            emptyList()
        }
    }

    override fun deleteCrash(crashId: String) {
        try {
            fileManager.removeItemAtPath("$crashesDir/$crashId.json", error = null)
        } catch (t: Throwable) {
            FloconLogger.logError("Error deleting crash", t)
        }
    }
}

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource {
    return FloconCrashReporterDataSourceIos()
}

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
    // called for any unhandled Kotlin exception, right before the app terminates
    setUnhandledExceptionHook { throwable ->
        try {
            onCrash(throwable)
        } catch (t: Throwable) {
            FloconLogger.logError("Error reporting crash", t)
        }
        terminateWithUnhandledException(throwable)
    }
}

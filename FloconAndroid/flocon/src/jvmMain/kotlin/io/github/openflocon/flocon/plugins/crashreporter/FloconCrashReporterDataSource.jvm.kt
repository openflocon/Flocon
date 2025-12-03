package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel

internal class FloconCrashReporterDataSourceJvm : FloconCrashReporterDataSource {
    override fun saveCrash(crash: CrashReportDataModel) {
        // No-op for JVM
    }

    override fun loadPendingCrashes(): List<CrashReportDataModel> {
        return emptyList()
    }

    override fun deleteCrash(crashId: String) {
        // No-op for JVM
    }
}

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource {
    return FloconCrashReporterDataSourceJvm()
}

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
    // No-op for JVM
}

internal actual fun getOsVersion(): String {
    return System.getProperty("os.name") ?: "Unknown"
}

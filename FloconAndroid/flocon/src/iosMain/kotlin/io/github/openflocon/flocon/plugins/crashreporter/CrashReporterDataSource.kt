package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel

internal actual fun buildFloconCrashReporterDataSource(context: io.github.openflocon.flocon.FloconContext): io.github.openflocon.flocon.plugins.crashreporter.FloconCrashReporterDataSource {
    return object : FloconCrashReporterDataSource {
        override fun saveCrash(crash: CrashReportDataModel) {
            // no op
        }
        override fun loadPendingCrashes(): List<CrashReportDataModel> {
            return emptyList()
        }
        override fun deleteCrash(crashId: String) {
            // no op
        }
    }
}

internal actual fun setupUncaughtExceptionHandler(
    context: io.github.openflocon.flocon.FloconContext,
    onCrash: (Throwable) -> Unit
) {
    // no op
}
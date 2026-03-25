package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource = object : FloconCrashReporterDataSource {
    override fun saveCrash(crash: CrashReportDataModel) {}
    override fun loadPendingCrashes(): List<CrashReportDataModel> = emptyList()
    override fun deleteCrash(crashId: String) {}
}

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
}

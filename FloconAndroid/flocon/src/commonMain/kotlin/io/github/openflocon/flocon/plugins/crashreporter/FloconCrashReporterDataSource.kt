package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel

internal interface FloconCrashReporterDataSource {
    fun saveCrash(crash: CrashReportDataModel)
    fun loadPendingCrashes(): List<CrashReportDataModel>
    fun deleteCrash(crashId: String)
}

internal expect fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource

internal expect fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
)

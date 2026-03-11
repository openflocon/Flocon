package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext

internal actual fun buildFloconCrashReporterDataSource(context: FloconContext): FloconCrashReporterDataSource {
    TODO("Not yet implemented")
}

internal actual fun setupUncaughtExceptionHandler(
    context: FloconContext,
    onCrash: (Throwable) -> Unit
) {
}
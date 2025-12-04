package io.github.openflocon.flocondesktop.features.crashreporter.model

sealed interface CrashReporterAction {
    data class Select(val crashId: String) : CrashReporterAction
}
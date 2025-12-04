package io.github.openflocon.flocondesktop.features.crashreporter.model

sealed interface CrashReporterAction {
    data class Select(val crashId: String) : CrashReporterAction
    data class Copy(val crash: CrashReporterSelectedUiModel) : CrashReporterAction
    data class Delete(val crashId: String) : CrashReporterAction
    data object Clean : CrashReporterAction
}
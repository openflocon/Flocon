package io.github.openflocon.flocondesktop.features.crashreporter.model

import androidx.compose.runtime.Immutable

@Immutable
data class CrashReporterSelectedUiModel(
    val id: String,
    val dateFormatted: String,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)

fun previewCrashReportSelectedItem() = CrashReporterSelectedUiModel(
    id = "id",
    dateFormatted = "12/10/2020 12:00:00",
    exceptionType = "ExceptionType",
    exceptionMessage = "ExceptionMessage",
    stackTrace = "stackTrace",
)

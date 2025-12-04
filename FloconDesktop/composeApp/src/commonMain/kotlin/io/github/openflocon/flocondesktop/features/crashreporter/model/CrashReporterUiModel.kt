package io.github.openflocon.flocondesktop.features.crashreporter.model

import androidx.compose.runtime.Immutable

@Immutable
data class CrashReporterUiModel(
    val id: String,
    val dateFormatted: String,
    val exceptionType: String,
    val exceptionMessage: String,
)

fun previewCrashReportItem() = CrashReporterUiModel(
    id = "id",
    dateFormatted = "12/10/2020 12:00:00",
    exceptionType = "ExceptionType",
    exceptionMessage = "ExceptionMessage",
)

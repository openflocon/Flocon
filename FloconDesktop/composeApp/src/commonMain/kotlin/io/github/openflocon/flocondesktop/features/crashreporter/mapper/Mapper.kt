package io.github.openflocon.flocondesktop.features.crashreporter.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel

fun CrashReportDomainModel.mapToUi() = CrashReporterUiModel(
    id = id,
    dateFormatted = formatDate(timestamp),
    exceptionType = exceptionType,
    exceptionMessage = exceptionMessage,
)

fun CrashReportDomainModel.mapToDetailUi() = CrashReporterSelectedUiModel(
    id = id,
    dateFormatted = formatDate(timestamp),
    exceptionType = exceptionType,
    exceptionMessage = exceptionMessage,
    stackTrace = stackTrace,
)
package com.flocon.data.remote.crashreporter.mapper

import com.flocon.data.remote.crashreporter.model.CrashReportDataModel
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel

internal fun CrashReportDataModel.toDomain() = CrashReportDomainModel(
    id = crashId,
    timestamp = timestamp,
    exceptionType = exceptionType,
    exceptionMessage = exceptionMessage,
    stackTrace = stackTrace,
)

package io.github.openflocon.domain.crashreporter.models

data class CrashReportDomainModel(
    val id: String,
    val timestamp: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)

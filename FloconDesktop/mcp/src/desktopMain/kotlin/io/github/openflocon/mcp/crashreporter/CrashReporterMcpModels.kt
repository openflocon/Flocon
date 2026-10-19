package io.github.openflocon.mcp.crashreporter

import kotlinx.serialization.Serializable

@Serializable
internal data class CrashReportsMcpResult(
    val deviceId: String,
    val packageName: String,
    val returned: Int,
    val hasMore: Boolean,
    val crashes: List<CrashReportSummaryMcpModel>,
)

/**
 * Without the stack trace, see [CrashReportMcpModel]
 */
@Serializable
internal data class CrashReportSummaryMcpModel(
    val crashId: String,
    val timestamp: String,
    val timestampMs: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val firstStackTraceLine: String?,
)

@Serializable
internal data class CrashReportMcpModel(
    val deviceId: String,
    val packageName: String,
    val crashId: String,
    val timestamp: String,
    val timestampMs: Long,
    val exceptionType: String,
    val exceptionMessage: String,
    val stackTrace: String,
)

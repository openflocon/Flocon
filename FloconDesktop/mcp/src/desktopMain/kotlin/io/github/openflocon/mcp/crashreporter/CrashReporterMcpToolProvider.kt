package io.github.openflocon.mcp.crashreporter

import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

internal class CrashReporterMcpToolProvider(
    private val crashReporterRepository: CrashReporterRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTypedTool(
            name = "list_crash_reports",
            title = "List crash reports",
            description = "Lists the crashes reported by the mobile app (exception type and message), newest first. " +
                "Use get_crash_report with a crashId to get the full stack trace.",
            annotations = readOnlyToolAnnotations,
            handler = ::listCrashReports,
        )

        server.addTypedTool(
            name = "get_crash_report",
            title = "Get a crash report",
            description = "Full details of a crash reported by the mobile app, including its stack trace.",
            annotations = readOnlyToolAnnotations,
            handler = ::getCrashReport,
        )
    }

    private suspend fun listCrashReports(input: ListCrashReportsInput): CrashReportsMcpResult {
        val target = targetResolver.resolve(input)
        val limit = input.limit.coerceIn(1, MAX_LIMIT)

        val crashes = crashReporterRepository.searchCrashReports(
            deviceIdAndPackageName = target,
            sinceTimestamp = input.sinceTimestamp,
            text = input.text?.takeIf { it.isNotBlank() },
            newestFirst = input.order == McpOrder.NewestFirst,
            limit = limit + 1,
        )
        val returned = crashes.take(limit)

        return CrashReportsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            returned = returned.size,
            hasMore = crashes.size > limit,
            crashes = returned.map { crash ->
                CrashReportSummaryMcpModel(
                    crashId = crash.id,
                    timestamp = Instant.fromEpochMilliseconds(crash.timestamp).toString(),
                    timestampMs = crash.timestamp,
                    exceptionType = crash.exceptionType,
                    exceptionMessage = crash.exceptionMessage,
                    firstStackTraceLine = crash.stackTrace.lineSequence().map { it.trim() }.firstOrNull { it.startsWith("at ") },
                )
            },
        )
    }

    private suspend fun getCrashReport(input: GetCrashReportInput): CrashReportMcpModel {
        val target = targetResolver.resolve(input)
        val crash = crashReporterRepository.observeCrashReportById(id = input.crashId, deviceIdAndPackageName = target).first()
            ?: throw McpToolException("Crash '${input.crashId}' not found. Use list_crash_reports to get the crash ids.")

        return CrashReportMcpModel(
            deviceId = target.deviceId,
            packageName = target.packageName,
            crashId = crash.id,
            timestamp = Instant.fromEpochMilliseconds(crash.timestamp).toString(),
            timestampMs = crash.timestamp,
            exceptionType = crash.exceptionType,
            exceptionMessage = crash.exceptionMessage,
            stackTrace = crash.stackTrace,
        )
    }
}

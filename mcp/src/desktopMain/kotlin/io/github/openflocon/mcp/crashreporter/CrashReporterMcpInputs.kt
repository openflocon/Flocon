package io.github.openflocon.mcp.crashreporter

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 20
internal const val MAX_LIMIT = 200

@Serializable
internal data class ListCrashReportsInput(
    @McpDescription("Only the crashes containing this text in their exception type, message or stack trace.")
    val text: String? = null,
    @McpDescription("Only the crashes after this epoch timestamp, in milliseconds.")
    val sinceTimestamp: Long? = null,
    @McpDescription("Sort order on the crash time. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of crashes returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class GetCrashReportInput(
    @McpDescription("Id of the crash (see list_crash_reports).")
    val crashId: String,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

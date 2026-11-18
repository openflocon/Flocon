package io.github.openflocon.mcp.analytics

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 50
internal const val MAX_LIMIT = 500

private const val ANALYTICS_TABLE_ID_DESCRIPTION =
    "Only the events of this table (see list_analytics_tables). Default: all the tables."
private const val EVENT_NAMES_DESCRIPTION = "Only the events with one of these exact names, ex: [\"screen_view\"]."
private const val TEXT_DESCRIPTION = "Only the events containing this text in their name, date or properties (names and values)."
private const val SINCE_TIMESTAMP_DESCRIPTION = "Only the events sent after this epoch timestamp, in milliseconds."
private const val INCLUDE_OLD_SESSIONS_DESCRIPTION = "Include the events of previous app launches. Default false."

/**
 * Filters shared by the tools reading the analytics events
 */
internal interface AnalyticsSearchArguments : McpTargetArguments {
    val analyticsTableId: String?
    val eventNames: List<String>?
    val text: String?
    val sinceTimestamp: Long?
    val includeOldSessions: Boolean
}

@Serializable
internal data class ListAnalyticsTablesInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListAnalyticsEventsInput(
    @McpDescription(ANALYTICS_TABLE_ID_DESCRIPTION)
    override val analyticsTableId: String? = null,
    @McpDescription(EVENT_NAMES_DESCRIPTION)
    override val eventNames: List<String>? = null,
    @McpDescription(TEXT_DESCRIPTION)
    override val text: String? = null,
    @McpDescription(SINCE_TIMESTAMP_DESCRIPTION)
    override val sinceTimestamp: Long? = null,
    @McpDescription(INCLUDE_OLD_SESSIONS_DESCRIPTION)
    override val includeOldSessions: Boolean = false,
    @McpDescription("Sort order on the event time. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of events returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : AnalyticsSearchArguments

@Serializable
internal data class CountAnalyticsEventsInput(
    @McpDescription(ANALYTICS_TABLE_ID_DESCRIPTION)
    override val analyticsTableId: String? = null,
    @McpDescription(EVENT_NAMES_DESCRIPTION)
    override val eventNames: List<String>? = null,
    @McpDescription(TEXT_DESCRIPTION)
    override val text: String? = null,
    @McpDescription(SINCE_TIMESTAMP_DESCRIPTION)
    override val sinceTimestamp: Long? = null,
    @McpDescription(INCLUDE_OLD_SESSIONS_DESCRIPTION)
    override val includeOldSessions: Boolean = false,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : AnalyticsSearchArguments

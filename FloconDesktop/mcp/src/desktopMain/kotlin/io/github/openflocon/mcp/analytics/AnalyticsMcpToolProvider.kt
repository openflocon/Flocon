package io.github.openflocon.mcp.analytics

import io.github.openflocon.domain.analytics.models.AnalyticsSearchDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.arrayProperty
import io.github.openflocon.mcp.booleanArg
import io.github.openflocon.mcp.booleanProperty
import io.github.openflocon.mcp.intArg
import io.github.openflocon.mcp.integerProperty
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.longArg
import io.github.openflocon.mcp.properties
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.safeToolCall
import io.github.openflocon.mcp.stringArg
import io.github.openflocon.mcp.stringListArg
import io.github.openflocon.mcp.stringProperty
import io.github.openflocon.mcp.targetProperties
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonObjectBuilder
import kotlin.time.Instant

internal class AnalyticsMcpToolProvider(
    private val analyticsRepository: AnalyticsRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTool(
            name = "list_analytics_tables",
            title = "List analytics tables",
            description = "Lists the analytics tables of the mobile app: one per analytics SDK / tracker sending its events to Flocon " +
                "(ex: \"Amplitude\", \"Firebase\").",
            inputSchema = ToolSchema(
                properties = properties {
                    targetProperties()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listTables(request)) }
        }

        server.addTool(
            name = "list_analytics_events",
            title = "List analytics events",
            description = "Lists the analytics events sent by the mobile app, with their properties, newest first. " +
                "By default only the current app session is returned (since the last app launch), from all the analytics tables.",
            inputSchema = ToolSchema(
                properties = properties {
                    searchProperties()
                    stringProperty(ARG_ORDER, "Sort order on the event time. Default $ORDER_NEWEST.", enum = listOf(ORDER_NEWEST, ORDER_OLDEST))
                    integerProperty(
                        ARG_LIMIT,
                        "Maximum number of events returned. Default $DEFAULT_LIMIT.",
                        minimum = 1,
                        maximum = MAX_LIMIT.toLong(),
                    )
                    targetProperties()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listEvents(request)) }
        }

        server.addTool(
            name = "count_analytics_events",
            title = "Count analytics events",
            description = "Counts the analytics events sent by the mobile app per event name, the most frequent first. " +
                "Useful to check which events were sent, and how many times. Same filters as list_analytics_events.",
            inputSchema = ToolSchema(
                properties = properties {
                    searchProperties()
                    targetProperties()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(countEvents(request)) }
        }
    }

    private suspend fun listTables(request: CallToolRequest): AnalyticsTablesMcpResult {
        val target = request.resolveTarget()
        return AnalyticsTablesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            analyticsTableIds = analyticsRepository.observeDeviceAnalytics(target).first().map { it.id },
        )
    }

    private suspend fun listEvents(request: CallToolRequest): AnalyticsEventsMcpResult {
        val target = request.resolveTarget()
        val limit = (request.intArg(ARG_LIMIT) ?: DEFAULT_LIMIT).coerceIn(1, MAX_LIMIT)

        val events = analyticsRepository.searchAnalytics(
            deviceIdAndPackageName = target,
            search = request.toSearch(target),
            newestFirst = request.newestFirst(),
            limit = limit + 1,
        )
        val returned = events.take(limit)

        return AnalyticsEventsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            currentSessionId = target.appInstance,
            returned = returned.size,
            hasMore = events.size > limit,
            events = returned.map { event ->
                AnalyticsEventMcpModel(
                    eventId = event.itemId,
                    analyticsTableId = event.analyticsTableId,
                    eventName = event.eventName,
                    timestamp = Instant.fromEpochMilliseconds(event.createdAt).toString(),
                    timestampMs = event.createdAt,
                    properties = event.properties.associate { it.name to it.value },
                    sessionId = event.appInstance,
                    isCurrentSession = event.appInstance == target.appInstance,
                )
            },
        )
    }

    private suspend fun countEvents(request: CallToolRequest): AnalyticsEventCountsMcpResult {
        val target = request.resolveTarget()
        val counts = analyticsRepository.countAnalyticsByEventName(
            deviceIdAndPackageName = target,
            search = request.toSearch(target),
        )
        return AnalyticsEventCountsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            total = counts.sumOf { it.count },
            events = counts.map { AnalyticsEventCountMcpModel(eventName = it.eventName, count = it.count) },
        )
    }

    private fun CallToolRequest.toSearch(target: DeviceIdAndPackageNameDomainModel) = AnalyticsSearchDomainModel(
        analyticsTableId = stringArg(ARG_ANALYTICS_TABLE_ID),
        text = stringArg(ARG_TEXT),
        eventNames = stringListArg(ARG_EVENT_NAMES)?.takeIf { it.isNotEmpty() },
        appInstance = target.appInstance.takeUnless { booleanArg(ARG_INCLUDE_OLD_SESSIONS) ?: false },
        sinceTimestamp = longArg(ARG_SINCE_TIMESTAMP),
    )

    private fun CallToolRequest.newestFirst(): Boolean = when (val order = stringArg(ARG_ORDER)) {
        null, ORDER_NEWEST -> true
        ORDER_OLDEST -> false
        else -> throw McpToolException("Unknown $ARG_ORDER '$order', expected $ORDER_NEWEST or $ORDER_OLDEST")
    }

    private suspend fun CallToolRequest.resolveTarget(): DeviceIdAndPackageNameDomainModel = targetResolver.resolve(
        deviceId = stringArg(ARG_DEVICE_ID),
        packageName = stringArg(ARG_PACKAGE_NAME),
    )

    private fun JsonObjectBuilder.searchProperties() {
        stringProperty(ARG_ANALYTICS_TABLE_ID, "Only the events of this table (see list_analytics_tables). Default: all the tables.")
        arrayProperty(ARG_EVENT_NAMES, "Only the events with one of these exact names, ex: [\"screen_view\"].", itemType = "string")
        stringProperty(ARG_TEXT, "Only the events containing this text in their name, date or properties (names and values).")
        integerProperty(ARG_SINCE_TIMESTAMP, "Only the events sent after this epoch timestamp, in milliseconds.")
        booleanProperty(ARG_INCLUDE_OLD_SESSIONS, "Include the events of previous app launches. Default false.")
    }

    private companion object {
        const val ARG_ANALYTICS_TABLE_ID = "analyticsTableId"
        const val ARG_EVENT_NAMES = "eventNames"
        const val ARG_TEXT = "text"
        const val ARG_SINCE_TIMESTAMP = "sinceTimestamp"
        const val ARG_INCLUDE_OLD_SESSIONS = "includeOldSessions"
        const val ARG_ORDER = "order"
        const val ARG_LIMIT = "limit"

        const val ORDER_NEWEST = "newest_first"
        const val ORDER_OLDEST = "oldest_first"

        const val DEFAULT_LIMIT = 50
        const val MAX_LIMIT = 500
    }
}

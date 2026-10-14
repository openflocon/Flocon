package io.github.openflocon.mcp.analytics

import io.github.openflocon.domain.analytics.models.AnalyticsSearchDomainModel
import io.github.openflocon.domain.analytics.repository.AnalyticsRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

internal class AnalyticsMcpToolProvider(
    private val analyticsRepository: AnalyticsRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTypedTool(
            name = "list_analytics_tables",
            title = "List analytics tables",
            description = "Lists the analytics tables of the mobile app: one per analytics SDK / tracker sending its events to Flocon " +
                "(ex: \"Amplitude\", \"Firebase\").",
            annotations = readOnlyToolAnnotations,
            handler = ::listTables,
        )

        server.addTypedTool(
            name = "list_analytics_events",
            title = "List analytics events",
            description = "Lists the analytics events sent by the mobile app, with their properties, newest first. " +
                "By default only the current app session is returned (since the last app launch), from all the analytics tables.",
            annotations = readOnlyToolAnnotations,
            handler = ::listEvents,
        )

        server.addTypedTool(
            name = "count_analytics_events",
            title = "Count analytics events",
            description = "Counts the analytics events sent by the mobile app per event name, the most frequent first. " +
                "Useful to check which events were sent, and how many times. Same filters as list_analytics_events.",
            annotations = readOnlyToolAnnotations,
            handler = ::countEvents,
        )
    }

    private suspend fun listTables(input: ListAnalyticsTablesInput): AnalyticsTablesMcpResult {
        val target = targetResolver.resolve(input)
        return AnalyticsTablesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            analyticsTableIds = analyticsRepository.observeDeviceAnalytics(target).first().map { it.id },
        )
    }

    private suspend fun listEvents(input: ListAnalyticsEventsInput): AnalyticsEventsMcpResult {
        val target = targetResolver.resolve(input)
        val limit = input.limit.coerceIn(1, MAX_LIMIT)

        val events = analyticsRepository.searchAnalytics(
            deviceIdAndPackageName = target,
            search = input.toSearch(target),
            newestFirst = input.order == McpOrder.NewestFirst,
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

    private suspend fun countEvents(input: CountAnalyticsEventsInput): AnalyticsEventCountsMcpResult {
        val target = targetResolver.resolve(input)
        val counts = analyticsRepository.countAnalyticsByEventName(
            deviceIdAndPackageName = target,
            search = input.toSearch(target),
        )
        return AnalyticsEventCountsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            total = counts.sumOf { it.count },
            events = counts.map { AnalyticsEventCountMcpModel(eventName = it.eventName, count = it.count) },
        )
    }

    private fun AnalyticsSearchArguments.toSearch(target: DeviceIdAndPackageNameDomainModel) = AnalyticsSearchDomainModel(
        analyticsTableId = analyticsTableId?.takeIf { it.isNotBlank() },
        text = text?.takeIf { it.isNotBlank() },
        eventNames = eventNames?.filter { it.isNotBlank() }?.takeIf { it.isNotEmpty() },
        appInstance = target.appInstance.takeUnless { includeOldSessions },
        sinceTimestamp = sinceTimestamp,
    )
}

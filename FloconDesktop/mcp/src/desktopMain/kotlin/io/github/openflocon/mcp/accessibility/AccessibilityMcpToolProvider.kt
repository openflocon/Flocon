package io.github.openflocon.mcp.accessibility

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import kotlin.time.Instant

internal class AccessibilityMcpToolProvider(
    private val accessibilityRepository: AccessibilityRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_accessibility_events",
            title = "List accessibility events",
            description = "Lists the accessibility events of the mobile app (what TalkBack / VoiceOver receive: focused elements, " +
                "announcements, content changes), with their text, content description, class and screen, newest first. " +
                "Can be filtered by type and by a text. Read only.",
            annotations = readOnlyToolAnnotations,
            handler = ::listEvents,
        )

        server.addTypedTool(
            name = "get_accessibility_state",
            title = "Get the accessibility state",
            description = "Returns what the device says about its assistive technologies: whether the automatic capture is supported, " +
                "whether a screen reader is running, touch exploration, and the running services. " +
                "Use it when list_accessibility_events is empty. Read only.",
            annotations = readOnlyToolAnnotations,
            handler = ::getState,
        )

        server.addTypedTool(
            name = "clear_accessibility_events",
            title = "Clear accessibility events",
            description = "Deletes the accessibility events received from the app. Useful to isolate the events of the next interaction.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::clearEvents,
        )
    }

    private suspend fun listEvents(input: ListAccessibilityEventsInput): AccessibilityEventsMcpResult {
        if (input.limit !in 1..ACCESSIBILITY_MAX_LIMIT) {
            throw McpToolException("limit must be between 1 and $ACCESSIBILITY_MAX_LIMIT")
        }
        val target = targetResolver.resolve(input)
        val events = accessibilityRepository.searchEvents(
            deviceIdAndPackageName = target,
            type = input.type?.takeIf { it.isNotBlank() },
            text = input.text?.takeIf { it.isNotBlank() },
            limit = input.limit + 1,
        )
        val returned = events.take(input.limit)

        return AccessibilityEventsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            types = accessibilityRepository.getTypes(target),
            returned = returned.size,
            hasMore = events.size > input.limit,
            events = returned.map { it.toMcp() },
        )
    }

    private suspend fun getState(input: GetAccessibilityStateInput): AccessibilityStateMcpResult {
        val target = targetResolver.resolve(input)
        val state = accessibilityRepository.getState(target)

        return AccessibilityStateMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            known = state != null,
            supported = state?.supported,
            enabled = state?.enabled,
            touchExploration = state?.touchExploration,
            services = state?.services,
        )
    }

    private suspend fun clearEvents(input: ClearAccessibilityEventsInput): ClearAccessibilityEventsMcpResult {
        val target = targetResolver.resolve(input)

        return ClearAccessibilityEventsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            deleted = accessibilityRepository.clearEvents(target),
        )
    }

    private fun AccessibilityEventDomainModel.toMcp() = AccessibilityEventMcpModel(
        eventId = id,
        type = type,
        text = text,
        contentDescription = contentDescription,
        className = className,
        screen = screen,
        bounds = bounds,
        extras = extras,
        timestamp = Instant.fromEpochMilliseconds(createdAt).toString(),
        timestampMs = createdAt,
    )
}

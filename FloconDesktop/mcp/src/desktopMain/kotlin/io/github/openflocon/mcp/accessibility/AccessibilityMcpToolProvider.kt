package io.github.openflocon.mcp.accessibility

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.accessibility.repository.AccessibilityRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
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
            name = "get_accessibility_focused_node",
            title = "Get the accessibility focused element",
            description = "Asks the device which element the screen reader (TalkBack) is on, with its text, content description, " +
                "class, bounds and position in the focus order of the screen. Android only, needs TalkBack on: " +
                "check get_accessibility_state if nothing is focused.",
            annotations = readOnlyToolAnnotations,
            handler = ::getFocusedNode,
        )

        server.addTypedTool(
            name = "move_accessibility_focus",
            title = "Move the accessibility focus",
            description = "Moves the accessibility focus of the device to the next or previous element of the screen, like a swipe " +
                "right / left with TalkBack, and returns the element it landed on. Use it to walk the focus order and check " +
                "what a screen reader reads. Android only, needs TalkBack on.",
            annotations = writeToolAnnotations(destructive = false, idempotent = false),
            handler = ::moveFocus,
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

    private suspend fun getFocusedNode(input: GetAccessibilityFocusedNodeInput): AccessibilityFocusedNodeMcpResult {
        val target = targetResolver.resolve(input)

        return focusedNodeAfter(target, reportMoved = false) { accessibilityRepository.requestFocusedNode(target) }
    }

    private suspend fun moveFocus(input: MoveAccessibilityFocusInput): AccessibilityFocusedNodeMcpResult {
        val next = when (input.direction.lowercase()) {
            "next" -> true
            "previous" -> false
            else -> throw McpToolException("direction must be \"next\" or \"previous\"")
        }
        val target = targetResolver.resolve(input)

        return focusedNodeAfter(target, reportMoved = true) {
            if (next) accessibilityRepository.focusNext(target) else accessibilityRepository.focusPrevious(target)
        }
    }

    /**
     * The device answers a request asynchronously: waits for the answer, and gives the current element when it never
     * comes because nothing changed (the answer is the same as before)
     */
    private suspend fun focusedNodeAfter(
        target: DeviceIdAndPackageNameDomainModel,
        reportMoved: Boolean,
        request: suspend () -> Unit,
    ): AccessibilityFocusedNodeMcpResult {
        val updates = accessibilityRepository.observeFocusedNode(target)
        val before = updates.first()

        val answer = coroutineScope {
            // collecting starts before the request is sent, so the answer can't be missed
            val answer = async(start = CoroutineStart.UNDISPATCHED) {
                withTimeoutOrNull(FOCUS_ANSWER_TIMEOUT) { updates.drop(1).first() }
            }
            request()
            answer.await()
        }
        val after = answer ?: updates.first()

        return after.toMcp(target, moved = if (reportMoved) after != before else null)
    }

    private fun AccessibilityFocusedNodeDomainModel?.toMcp(
        target: DeviceIdAndPackageNameDomainModel,
        moved: Boolean?,
    ): AccessibilityFocusedNodeMcpResult {
        if (this == null || !found) {
            return AccessibilityFocusedNodeMcpResult(
                deviceId = target.deviceId,
                packageName = target.packageName,
                focused = false,
                moved = moved,
            )
        }
        return AccessibilityFocusedNodeMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            focused = true,
            moved = moved,
            text = text,
            contentDescription = contentDescription,
            className = className,
            screen = screen,
            bounds = bounds,
            extras = extras,
            position = index?.plus(1),
            count = count,
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

private val FOCUS_ANSWER_TIMEOUT = 1500.milliseconds

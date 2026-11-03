package io.github.openflocon.mcp.screen

import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.domain.screen.ScreenRequestStore
import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.timeline.usecase.SetTimelineGraphUseCase
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.writeToolAnnotations

private const val TIMELINE_VIEW_GRAPH = "graph"
private const val TIMELINE_VIEW_LIST = "list"

internal class NavigateDesktopAppMcpToolProvider(
    private val targetResolver: McpTargetResolver,
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val selectDeviceAppUseCase: SelectDeviceAppUseCase,
    private val setTimelineGraphUseCase: SetTimelineGraphUseCase,
    private val screenRequestStore: ScreenRequestStore,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "floconNavigateIntoDesktopApp",
            title = "Navigate into the Flocon desktop app",
            description = "Moves the Flocon Desktop window for the user: displays a screen, selects the device and app, " +
                "and sets the timeline view (graph or list). Unlike the other tools it changes what the user sees and selected in Flocon " +
                "(the timeline view is saved in their preferences). Flocon is not brought to the front: " +
                "tell the user to go to the Flocon desktop app to see it. Give at least one parameter.",
            annotations = writeToolAnnotations(destructive = false, idempotent = true),
            handler = ::navigate,
        )
    }

    private suspend fun navigate(input: NavigateDesktopAppInput): NavigateDesktopAppMcpResult {
        val screenName = input.screen?.takeIf { it.isNotBlank() }
        val deviceId = input.deviceId?.takeIf { it.isNotBlank() }
        val packageName = input.packageName?.takeIf { it.isNotBlank() }
        val timelineView = input.timelineView?.takeIf { it.isNotBlank() }

        if (screenName == null && deviceId == null && packageName == null && timelineView == null) {
            throw McpToolException("Nothing to do: give at least one of screen, deviceId, packageName or timelineView.")
        }

        // everything is checked before anything is changed
        val screen = screenName?.let { name ->
            FloconScreen.entries.firstOrNull { it.toToolName() == name.lowercase() }
                ?: throw McpToolException("Unknown screen '$name'. Available screens: ${FloconScreen.entries.joinToString { it.toToolName() }}.")
        }
        val graph = timelineView?.let { view ->
            when (view.lowercase()) {
                TIMELINE_VIEW_GRAPH -> true
                TIMELINE_VIEW_LIST -> false
                else -> throw McpToolException("Unknown timelineView '$view'. Use '$TIMELINE_VIEW_GRAPH' or '$TIMELINE_VIEW_LIST'.")
            }
        }
        val target = if (deviceId != null || packageName != null) targetResolver.resolve(deviceId, packageName) else null
        val currentTarget = if (target != null) runCatching { targetResolver.resolve(null, null) }.getOrNull() else null

        val applied = buildList {
            if (target != null) {
                if (currentTarget?.deviceId != target.deviceId) {
                    selectDeviceUseCase(target.deviceId)
                    add("Selected the device ${target.deviceId}")
                }
                if (packageName != null) {
                    selectDeviceAppUseCase(target.packageName)
                    add("Selected the app ${target.packageName}")
                }
            }
            if (graph != null) {
                setTimelineGraphUseCase(graph)
                add("Set the timeline view to ${if (graph) TIMELINE_VIEW_GRAPH else TIMELINE_VIEW_LIST}")
            }
            if (screen != null) {
                screenRequestStore.request(screen)
                add("Requested the screen ${screen.toToolName()}")
            }
        }

        return NavigateDesktopAppMcpResult(
            applied = applied,
            message = if (applied.isEmpty()) {
                "Flocon already displays this selection, nothing changed."
            } else {
                "Done. Flocon is not brought to the front: ask the user to go to the Flocon desktop app to see it."
            },
        )
    }
}

// AdbCommander -> adb_commander
private fun FloconScreen.toToolName(): String = name.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()

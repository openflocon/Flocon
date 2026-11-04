package io.github.openflocon.mcp.timeline

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

@Serializable
internal data class ShowInTimelineInput(
    @McpDescription(
        "An event id of a get_timeline answer (source:id, for example network:abc or crash:xyz): the timeline selects it " +
            "and shows the seconds around it.",
    )
    val eventId: String? = null,
    @McpDescription(
        "Which part of the timeline to show, same as get_timeline: recent, session, before_crash, at, range. " +
            "Omit everything to show the latest crash, otherwise the main anomaly (error, loop, large response) of the last 2 minutes.",
    )
    val mode: TimelineMode? = null,
    @McpDescription("Time for the mode at: epoch milliseconds, an ISO-8601 instant with an offset, or HH:mm[:ss]. The times are those of the phone.")
    val at: String? = null,
    @McpDescription("Start of the mode range, same formats as `at`.")
    val from: String? = null,
    @McpDescription("End of the mode range, same formats as `at`. Defaults to the last event.")
    val to: String? = null,
    @McpDescription("Half width in seconds of the zone shown around the event or the time. Default $DEFAULT_AROUND_SECONDS.")
    val aroundSeconds: Int = DEFAULT_AROUND_SECONDS,
    @McpDescription(
        "graph (default) or list. This changes the timeline view saved in the preferences of the user. " +
            "The list selects the row of the event but does not scroll to it.",
    )
    val view: String = SHOW_VIEW_GRAPH,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

internal const val SHOW_VIEW_GRAPH = "graph"
internal const val SHOW_VIEW_LIST = "list"

package io.github.openflocon.mcp.timeline

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val DEFAULT_AROUND_SECONDS = 30
internal const val DEFAULT_RECENT_MINUTES = 5
internal const val DEFAULT_MAX_LINES = 150
internal const val MAX_LINES = 400

@Serializable
internal enum class TimelineMode {
    @SerialName("recent")
    Recent,

    @SerialName("session")
    Session,

    @SerialName("before_crash")
    BeforeCrash,

    @SerialName("at")
    At,

    @SerialName("range")
    Range,
}

@Serializable
internal data class GetTimelineInput(
    @McpDescription(
        "Which part of the timeline to read: recent (the last `minutes`), session (the latest session of the app), " +
            "before_crash (the minute before the latest crash), at (around the time `at`), range (from `from` to `to`). " +
            "Omit it to let the tool choose: the latest crash if there is one, otherwise the last 2 minutes of the latest session. " +
            "The answer always tells which window was chosen and why.",
    )
    val mode: TimelineMode? = null,
    @McpDescription(
        "Time for the mode at: epoch milliseconds, an ISO-8601 instant with an offset (2026-11-03T14:32:00+01:00), " +
            "or HH:mm[:ss] (the latest past occurrence of that time, in the time zone of the computer). " +
            "The times are those of the phone: they can differ from the computer clock.",
    )
    val at: String? = null,
    @McpDescription("Start of the mode range, same formats as `at`.")
    val from: String? = null,
    @McpDescription("End of the mode range, same formats as `at`. Defaults to the last event.")
    val to: String? = null,
    @McpDescription("An event id of a previous answer (source:id, for example network:abc): reads around that event. Not with mode, at, from or to.")
    val eventId: String? = null,
    @McpDescription("Half width in seconds of the window for at and eventId. Default $DEFAULT_AROUND_SECONDS.")
    val aroundSeconds: Int = DEFAULT_AROUND_SECONDS,
    @McpDescription("Length in minutes for the mode recent. Default $DEFAULT_RECENT_MINUTES.")
    val minutes: Int = DEFAULT_RECENT_MINUTES,
    @McpDescription(
        "Text to look for in the events of the window (event name, url, query...). The matches are always returned, " +
            "even when the answer is truncated: use it to know if an event was sent (found tells it).",
    )
    val search: String? = null,
    @McpDescription("Only look for the text in this source: network, database, analytics, accessibility, webview, crash, table, deeplink, adb.")
    val searchSource: String? = null,
    @McpDescription(
        "Only read these sources: network, database, analytics, accessibility, webview, crash, table, deeplink, adb. " +
            "Default: all of them. Crashes of the window are not listed if crash is left out.",
    )
    val sources: List<String>? = null,
    @McpDescription(
        "Leave these sources out of the window, same names as `sources`. Use it to drop the noise of a source, " +
            "for example the transactions of a database library: [\"database\"].",
    )
    val excludeSources: List<String>? = null,
    @McpDescription("Maximum number of lines returned, from 1 to $MAX_LINES. Default $DEFAULT_MAX_LINES.")
    val maxLines: Int = DEFAULT_MAX_LINES,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

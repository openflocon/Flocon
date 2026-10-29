package io.github.openflocon.flocondesktop.features.timeline.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.options.NetworkItemOption

@Immutable
internal data class TimelineRowUiModel(
    val id: String,
    val source: TimelineSource,
    val day: String, // dd/MM/yyyy, to group the events by day
    val time: String,
    val timestamp: Long, // to compute the time since the previous event
    val title: String,
    val subtitle: String?,
    val network: TimelineNetworkUiModel? = null, // network calls only
    val sessionStart: Long? = null, // the start of the session of the event, null if none is known
    val phase: TimelinePhase = TimelinePhase.Request, // network calls only: the request and the response are two rows
) {
    // the ids of two sources can be the same. Shared by the request and the response of a network call,
    // to select and highlight them together
    val callKey: String get() = "${source.name}:$id"

    // unique in the list
    val key: String get() = if (phase == TimelinePhase.Request) callKey else "$callKey:${phase.name}"
}

@Immutable
internal data class TimelineNetworkUiModel(
    val method: String,
    val host: String?,
    val path: String,
    val statusCode: Int?, // null while waiting for the response, or without http code
    val duration: String?,
    val durationMs: Double? = null, // to color the slow calls
    // a gRPC or a WebSocket call has no http request to copy as cURL, replay or mock
    val httpActions: Boolean = true,
    val isImage: Boolean = false, // the response is an image
)

@Immutable
internal sealed interface TimelineItemUiModel {
    val key: String

    data class Row(val row: TimelineRowUiModel) : TimelineItemUiModel {
        override val key: String get() = row.key
    }

    // displayed above the first event of each day
    data class DayHeader(val day: String, override val key: String) : TimelineItemUiModel
}

@Immutable
internal data class TimelineSettingsUiModel(
    val autoScroll: Boolean,
    val invertList: Boolean,
    val pinPanel: Boolean,
)

internal fun previewTimelineSettingsUiModel() = TimelineSettingsUiModel(
    autoScroll = false,
    invertList = false,
    pinPanel = false,
)

internal sealed interface TimelineAction {
    data class FilterTextChanged(val text: String) : TimelineAction
    data class ToggleSource(val source: TimelineSource) : TimelineAction
    data class ToggleFacet(val facet: TimelineFacet, val value: String) : TimelineAction
    data class SetOnlyWithEvents(val value: Boolean) : TimelineAction
    data object ClearFilters : TimelineAction
    data object TogglePanel : TimelineAction
    data class Open(val row: TimelineRowUiModel) : TimelineAction
    data class ToggleAutoScroll(val value: Boolean) : TimelineAction
    data class InvertList(val value: Boolean) : TimelineAction
    data class PinPanel(val value: Boolean) : TimelineAction
    data class NetworkOption(val callId: String, val option: NetworkItemOption) : TimelineAction
}

internal fun previewTimelineRowUiModel(
    source: TimelineSource = TimelineSource.Network,
    id: String = "1",
    title: String = "title",
    subtitle: String? = null,
    network: TimelineNetworkUiModel? = null,
    phase: TimelinePhase = TimelinePhase.Request,
) = TimelineRowUiModel(
    id = id,
    source = source,
    day = "20/10/2026",
    time = "12:00:00.000",
    timestamp = 0L,
    title = title,
    subtitle = subtitle,
    network = network,
    phase = phase,
)

// one row of each kind of event
internal fun previewTimelineRows(): List<TimelineRowUiModel> = listOf(
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n1",
        title = "https://api.acme.io/v1/users?id=2",
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users?id=2", statusCode = null, duration = null),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n1",
        title = "https://api.acme.io/v1/users?id=2",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users?id=2", statusCode = 200, duration = "124 ms", durationMs = 124.0, isImage = true),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n2",
        title = "https://api.acme.io/v1/orders",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "POST", host = "api.acme.io", path = "/v1/orders", statusCode = 500, duration = "3.4 s", durationMs = 3_400.0),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n4",
        title = "https://api.acme.io/v1/users/42",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GET", host = "api.acme.io", path = "/v1/users/42", statusCode = 404, duration = "87 ms", durationMs = 87.0),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Network,
        id = "n3",
        title = "https://api.acme.io/acme.Users/List",
        subtitle = "OK",
        phase = TimelinePhase.Response,
        network = TimelineNetworkUiModel(method = "GRPC", host = "api.acme.io", path = "/acme.Users/List", statusCode = null, duration = "48 ms"),
    ),
    previewTimelineRowUiModel(
        source = TimelineSource.Database,
        id = "1",
        title = "SELECT id, name FROM users WHERE age > 18 AND name = 'bob' ORDER BY name LIMIT 20",
        subtitle = "app.db",
    ),
    previewTimelineRowUiModel(source = TimelineSource.Analytics, id = "a1", title = "screen_view", subtitle = "Firebase"),
    previewTimelineRowUiModel(source = TimelineSource.Accessibility, id = "ac1", title = "Pay now", subtitle = "Button"),
    previewTimelineRowUiModel(source = TimelineSource.WebView, id = "w1", title = "Uncaught TypeError: x is undefined", subtitle = "error"),
    previewTimelineRowUiModel(
        source = TimelineSource.Crash,
        id = "c1",
        title = "java.lang.IllegalStateException",
        subtitle = "Cannot read the cart before it is loaded",
    ),
    previewTimelineRowUiModel(source = TimelineSource.Table, id = "t1", title = "Cart", subtitle = "items=3, total=42.0"),
    previewTimelineRowUiModel(source = TimelineSource.Deeplink, id = "d1", title = "acme://product/42", subtitle = "Product"),
    previewTimelineRowUiModel(source = TimelineSource.Adb, id = "adb1", title = "adb shell pm list packages", subtitle = "package:com.acme.app"),
    previewTimelineRowUiModel(source = TimelineSource.AppStart, id = "s1"),
)

// a day of events, newest first, then the start of the session
internal fun previewTimelineItems(): List<TimelineItemUiModel> {
    val rows = previewTimelineRows()
    return buildList {
        add(TimelineItemUiModel.DayHeader(day = "20/10/2026", key = "day:20/10/2026"))
        rows.forEach { add(TimelineItemUiModel.Row(it)) }
    }
}

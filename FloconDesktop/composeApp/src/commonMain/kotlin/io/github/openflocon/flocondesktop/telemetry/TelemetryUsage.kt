package io.github.openflocon.flocondesktop.telemetry

/**
 * Screens named in the `screen` property of `screen_viewed`, `feature_used` and `setting_changed`,
 * so one breakdown by screen works across the three events. The menu entries are sent by their menu id
 * (`Network`, `Database`...), the names below are the screens that are not a menu entry.
 */
enum class TelemetryScreen(val wireName: String) {
    NetworkMocks("NetworkMocks"),
    NetworkBadQuality("NetworkBadQuality"),
    NetworkDetail("NetworkDetail"),
    Timeline("Timeline"),
    Settings("Settings"),
}

/**
 * Actions sent with `feature_used` (property `feature`). Bounded list: never a name, a URL, a body or a token.
 * The screen is part of the entry so it can't drift or be forgotten at the call site.
 */
enum class TelemetryFeature(val wireName: String, val screen: TelemetryScreen) {
    MockCreateClicked("mock_create_clicked", TelemetryScreen.NetworkMocks),
    MockToggled("mock_toggled", TelemetryScreen.NetworkMocks),
    MockImported("mock_imported", TelemetryScreen.NetworkMocks),
    MockExported("mock_exported", TelemetryScreen.NetworkMocks),
    BadNetworkCreateClicked("bad_network_create_clicked", TelemetryScreen.NetworkBadQuality),
    BadNetworkToggled("bad_network_toggled", TelemetryScreen.NetworkBadQuality),
    DetailOpenExternallyRequest("detail_open_externally_request", TelemetryScreen.NetworkDetail),
    DetailOpenExternallyResponse("detail_open_externally_response", TelemetryScreen.NetworkDetail),
    DetailShareMarkdown("detail_share_markdown", TelemetryScreen.NetworkDetail),
    DetailSaveHtml("detail_save_html", TelemetryScreen.NetworkDetail),
    DetailCopyCurl("detail_copy_curl", TelemetryScreen.NetworkDetail),
    DetailCopyText("detail_copy_text", TelemetryScreen.NetworkDetail),
    DetailCopyImage("detail_copy_image", TelemetryScreen.NetworkDetail),
    DetailSaveImage("detail_save_image", TelemetryScreen.NetworkDetail),
    DetailDiffWithClipboard("detail_diff_with_clipboard", TelemetryScreen.NetworkDetail),
    DetailJsonDetail("detail_json_detail", TelemetryScreen.NetworkDetail),
    DetailBearerJwt("detail_bearer_jwt", TelemetryScreen.NetworkDetail),

    /** Its `screen` is the documentation page, set by the caller (see `featureUsed(screen = ...)`). */
    HelpOpened("help_opened", TelemetryScreen.Settings),
}

/** Settings sent with `setting_changed` (property `setting`, plus `value`: a boolean or the new enum value). */
enum class TelemetrySetting(val wireName: String, val screen: TelemetryScreen) {
    TimelineView("timeline_view", TelemetryScreen.Timeline),
    TimelineAutoScroll("timeline_auto_scroll", TelemetryScreen.Timeline),
    TimelineScreenshots("timeline_screenshots", TelemetryScreen.Timeline),
    TimelineMergePlugins("timeline_merge_plugins", TelemetryScreen.Timeline),
    MascotEnabled("mascot_enabled", TelemetryScreen.Settings),
}

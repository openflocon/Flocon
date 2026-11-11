package io.github.openflocon.flocondesktop.telemetry

/**
 * Exhaustive list of events sent by Flocon Desktop.
 * Keep docs2/telemetry.md in sync when adding one.
 */
object TelemetryEvents {
    /**
     * Sent once per launch, with a snapshot of the settings (see `AppStartedTelemetry`): `timeline_view`,
     * `timeline_auto_scroll`, `timeline_screenshots`, `timeline_merge_plugins`, `mascot_enabled`,
     * `enabled_mocks_count`, `bad_network_enabled`. A value that can't be read quickly is left out.
     */
    const val APP_STARTED = "app_started"

    /**
     * A menu entry or a sub-screen was opened. Property: `screen` (e.g. "Network", "Database", or a [TelemetryScreen]:
     * "NetworkMocks", "NetworkBadQuality", "NetworkDetail", sent at most once per launch for the last one).
     */
    const val SCREEN_VIEWED = "screen_viewed"

    /**
     * An app with the Flocon SDK connected, once per app launch.
     * Properties: `platform`, `sdk`, `sdk_version`, `connection_type`. Never the device or app name.
     */
    const val DEVICE_CONNECTED = "device_connected"

    /**
     * An action was used. Properties: `feature` ([TelemetryFeature]), `screen` (same values as `screen_viewed`,
     * the documentation page for `help_opened`), and when relevant `value` (boolean), `source` (`help_opened` only,
     * see `HelpSource`), `enabled_mocks_count` (`mock_toggled` only). Never user data.
     */
    const val FEATURE_USED = "feature_used"

    /** A setting was changed. Properties: `setting` ([TelemetrySetting]), `value` (boolean or enum value), `screen`. */
    const val SETTING_CHANGED = "setting_changed"
}

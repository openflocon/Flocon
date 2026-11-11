package io.github.openflocon.flocondesktop.telemetry

/**
 * Exhaustive list of events sent by Flocon Desktop.
 * Keep docs2/telemetry.md in sync when adding one.
 */
object TelemetryEvents {
    /** Sent once per launch. */
    const val APP_STARTED = "app_started"

    /** A menu entry was opened. Property: `screen` (e.g. "Network", "Database"). */
    const val SCREEN_VIEWED = "screen_viewed"

    /**
     * An app with the Flocon SDK connected, once per app launch.
     * Properties: `platform`, `sdk`, `sdk_version`, `connection_type`. Never the device or app name.
     */
    const val DEVICE_CONNECTED = "device_connected"
}

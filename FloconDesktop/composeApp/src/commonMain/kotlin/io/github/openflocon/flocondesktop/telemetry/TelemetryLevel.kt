package io.github.openflocon.flocondesktop.telemetry

/**
 * How much usage data Flocon Desktop is allowed to send.
 *
 * See docs/telemetry.md for the exact list of collected events.
 */
enum class TelemetryLevel {
    /**
     * A random identifier is stored on this machine so returning users can be counted.
     * Only used after an explicit opt-in.
     */
    Full,

    /**
     * Default. A random identifier is generated in memory for each launch and never written to disk:
     * Flocon can count sessions and feature usage, but cannot recognise the same user across launches.
     */
    Anonymous,

    /** Nothing is sent. */
    Disabled,
}

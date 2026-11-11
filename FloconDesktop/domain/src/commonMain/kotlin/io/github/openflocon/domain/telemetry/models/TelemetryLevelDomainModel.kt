package io.github.openflocon.domain.telemetry.models

/**
 * How much usage data Flocon Desktop is allowed to send.
 *
 * See docs2/telemetry.md for the exact list of collected events.
 */
enum class TelemetryLevelDomainModel {
    /**
     * A random identifier is stored on this machine so returning users can be counted.
     * Only used after an explicit opt-in.
     */
    Full,

    /**
     * A random identifier is generated in memory for each launch and never written to disk:
     * Flocon can count sessions and feature usage, but cannot recognise the same user across launches.
     */
    Anonymous,

    /** Nothing is sent. Also the effective level until the user answers the consent prompt. */
    Disabled,
}

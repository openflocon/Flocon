package io.github.openflocon.domain.telemetry.models

/**
 * Build/runtime configuration of the telemetry.
 *
 * The PostHog key is injected at build time from the `POSTHOG_API_KEY` environment variable
 * (GitHub secret on the release workflow). Local builds and forks have no key, so telemetry is
 * fully turned off for them and the consent prompt is never shown.
 */
data class TelemetryConfigDomainModel(
    val apiKey: String,
    val host: String,
    val appVersion: String,
    val doNotTrack: Boolean,
    val osName: String = System.getProperty("os.name").orEmpty(),
    val osArch: String = System.getProperty("os.arch").orEmpty(),
) {
    /** Telemetry can only run on official builds, and never when the user set DO_NOT_TRACK. */
    val isAvailable: Boolean get() = apiKey.isNotBlank() && !doNotTrack
}

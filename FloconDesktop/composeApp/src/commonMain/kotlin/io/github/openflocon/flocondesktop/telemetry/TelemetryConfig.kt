package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.flocondesktop.BuildConfig

/**
 * Build/runtime configuration of the telemetry.
 *
 * The PostHog key is injected at build time from the `POSTHOG_API_KEY` environment variable
 * (GitHub secret on the release workflow). Local builds and forks have no key, so telemetry is
 * fully turned off for them and the consent prompt is never shown.
 */
data class TelemetryConfig(
    val apiKey: String,
    val host: String,
    val appVersion: String,
    val doNotTrack: Boolean,
) {
    /** Telemetry can only run on official builds, and never when the user set DO_NOT_TRACK. */
    val isAvailable: Boolean get() = apiKey.isNotBlank() && !doNotTrack

    companion object {
        fun fromBuildConfig(
            getEnv: (String) -> String? = System::getenv,
        ): TelemetryConfig = TelemetryConfig(
            apiKey = BuildConfig.POSTHOG_API_KEY,
            host = BuildConfig.POSTHOG_HOST,
            appVersion = BuildConfig.APP_VERSION,
            doNotTrack = isDoNotTrack(getEnv("DO_NOT_TRACK")),
        )

        /** Follows https://consoledonottrack.com: any value other than empty, "0" or "false" opts out. */
        fun isDoNotTrack(value: String?): Boolean {
            val normalized = value?.trim()?.lowercase() ?: return false
            return normalized.isNotEmpty() && normalized != "0" && normalized != "false"
        }
    }
}

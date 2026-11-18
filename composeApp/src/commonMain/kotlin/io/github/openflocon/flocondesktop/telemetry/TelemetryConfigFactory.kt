package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.telemetry.models.TelemetryConfigDomainModel
import io.github.openflocon.flocondesktop.BuildConfig

/**
 * The PostHog key is injected at build time from the `POSTHOG_API_KEY` environment variable
 * (GitHub secret on the release workflow). Local builds and forks have no key.
 */
fun telemetryConfigFromBuildConfig(
    getEnv: (String) -> String? = System::getenv,
): TelemetryConfigDomainModel = TelemetryConfigDomainModel(
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

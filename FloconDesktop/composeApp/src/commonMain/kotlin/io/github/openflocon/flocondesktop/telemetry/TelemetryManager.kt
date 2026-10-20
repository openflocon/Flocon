package io.github.openflocon.flocondesktop.telemetry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class TelemetryState(
    /** `false` on builds without an API key, or when DO_NOT_TRACK is set: nothing is ever sent. */
    val isAvailable: Boolean,
    val doNotTrack: Boolean,
    /** `true` once the user answered the consent prompt. */
    val hasChosen: Boolean,
    val level: TelemetryLevel,
) {
    val shouldAskConsent: Boolean get() = isAvailable && !hasChosen
}

/**
 * Single entry point for usage analytics on Flocon Desktop.
 *
 * Only call [track] with feature-level information (screen names, version, OS).
 * Never pass anything coming from the inspected apps: URLs, payloads, package names, database content...
 */
class TelemetryManager(
    private val preferences: TelemetryPreferences,
    private val client: TelemetryClient,
    private val applicationScope: CoroutineScope,
    private val config: TelemetryConfig,
    private val osName: String = System.getProperty("os.name").orEmpty(),
    private val osArch: String = System.getProperty("os.arch").orEmpty(),
) {

    /** Regenerated on every launch and never written to disk. */
    private val sessionId: String = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(computeState())
    val state: StateFlow<TelemetryState> = _state.asStateFlow()

    fun setLevel(level: TelemetryLevel) {
        preferences.level = level
        if (level != TelemetryLevel.Full) {
            // Consent withdrawn (or never given): the persistent identifier must not survive.
            preferences.installationId = null
        }
        _state.value = computeState()
    }

    fun track(event: String, properties: Map<String, Any> = emptyMap()) {
        val current = _state.value
        if (!current.isAvailable) return

        val distinctId = when (current.level) {
            TelemetryLevel.Disabled -> return
            TelemetryLevel.Anonymous -> sessionId
            TelemetryLevel.Full -> preferences.installationId
                ?: UUID.randomUUID().toString().also { preferences.installationId = it }
        }

        val allProperties = properties + mapOf(
            // No person profiles: cheaper on PostHog and nothing is attached to a "user".
            "\$process_person_profile" to false,
            "\$session_id" to sessionId,
            // Don't resolve the request IP into a location.
            "\$geoip_disable" to true,
            "\$lib" to "flocon-desktop",
            "app_version" to config.appVersion,
            "os_name" to osName,
            "os_arch" to osArch,
            "telemetry_level" to current.level.name,
        )

        applicationScope.launch {
            client.capture(event = event, distinctId = distinctId, properties = allProperties)
        }
    }

    private fun computeState(): TelemetryState {
        val chosen = preferences.level
        return TelemetryState(
            isAvailable = config.isAvailable,
            doNotTrack = config.doNotTrack,
            hasChosen = chosen != null,
            level = when {
                !config.isAvailable -> TelemetryLevel.Disabled
                else -> chosen ?: TelemetryLevel.Anonymous
            },
        )
    }
}

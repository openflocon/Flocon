package com.flocon.data.remote.mock

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Data replayed by the mock mode (`--mock=<name>`) as if devices were connected.
 * Written with the DSL in `MockScenarioDsl.kt`, listed in [MockScenarios].
 */
data class MockScenario(
    val name: String,
    val description: String,
    val devices: List<MockDevice>,
)

data class MockDevice(
    val id: String,
    val name: String,
    val platform: String, // "android" or "ios"
    val appName: String,
    val packageName: String,
    val appStartedAt: Duration?, // how long before the launch the app started, null = just before its first event
    val sdkVersion: String?, // the Flocon SDK version on the device, null = the desktop version
    val events: List<MockEvent>,
)

/**
 * The SDK start timestamp (ms) that identifies the app launch: the desktop draws the session from it.
 * By default the app started 1 s before its earliest event.
 */
internal fun MockDevice.appInstance(launchTime: Instant): Long {
    val startedAt = appStartedAt ?: ((events.maxOfOrNull { it.at } ?: Duration.ZERO) + 1.seconds)
    return (launchTime - startedAt).toEpochMilliseconds()
}

sealed interface MockEvent {
    /** How long before the app launch the event happened. */
    val at: Duration

    data class NetworkCall(
        override val at: Duration,
        val method: String,
        val url: String,
        val status: Int = 200,
        val duration: Duration = 100.milliseconds,
        val requestHeaders: Map<String, String> = emptyMap(),
        val requestBody: String? = null,
        val responseHeaders: Map<String, String> = emptyMap(),
        val responseBody: String? = null,
        val contentType: String? = null,
        val error: String? = null, // the call failed without an http response (timeout, no network…)
        val pending: Boolean = false, // no response yet
    ) : MockEvent

    data class TimelineLog(
        override val at: Duration,
        val value: String,
        val type: String = "log",
        val level: String? = "info",
        val params: Map<String, String> = emptyMap(),
    ) : MockEvent

    data class Crash(
        override val at: Duration,
        val exceptionType: String,
        val message: String,
        val stackTrace: String = "",
    ) : MockEvent

    data class AnalyticsEvent(
        override val at: Duration,
        val eventName: String,
        val tableId: String = "analytics",
        val properties: Map<String, String> = emptyMap(),
    ) : MockEvent

    data class FeatureFlags(
        override val at: Duration,
        val provider: String,
        val flags: List<Flag>,
    ) : MockEvent {
        data class Flag(
            val name: String,
            val state: String, // "on", "off" or "custom"
            val variant: String,
            val payload: String? = null,
        )
    }

    /** A message sent as is, to test what a real SDK should not send (invalid body…). */
    data class Raw(
        override val at: Duration,
        val plugin: String,
        val method: String,
        val body: String,
    ) : MockEvent
}

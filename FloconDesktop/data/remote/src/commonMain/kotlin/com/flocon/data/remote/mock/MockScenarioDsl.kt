package com.flocon.data.remote.mock

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@DslMarker
annotation class MockScenarioDslMarker

/** Readability only: `5.minutes.ago` is the time before the launch. */
val Duration.ago: Duration get() = this

fun scenario(
    name: String,
    description: String = "",
    block: MockScenarioBuilder.() -> Unit,
): MockScenario = MockScenario(
    name = name,
    description = description,
    devices = MockScenarioBuilder().apply(block).devices.toList(),
)

@MockScenarioDslMarker
class MockScenarioBuilder internal constructor() {
    internal val devices = mutableListOf<MockDevice>()

    fun android(
        name: String,
        app: String,
        packageName: String,
        id: String = name.toDeviceId(),
        appStartedAt: Duration? = null,
        sdkVersion: String? = null,
        block: MockDeviceBuilder.() -> Unit,
    ) = device("android", name, app, packageName, id, appStartedAt, sdkVersion, block)

    fun ios(
        name: String,
        app: String,
        packageName: String,
        id: String = name.toDeviceId(),
        appStartedAt: Duration? = null,
        sdkVersion: String? = null,
        block: MockDeviceBuilder.() -> Unit,
    ) = device("ios", name, app, packageName, id, appStartedAt, sdkVersion, block)

    private fun device(
        platform: String,
        name: String,
        app: String,
        packageName: String,
        id: String,
        appStartedAt: Duration?,
        sdkVersion: String?,
        block: MockDeviceBuilder.() -> Unit,
    ) {
        devices += MockDevice(
            id = id,
            name = name,
            platform = platform,
            appName = app,
            packageName = packageName,
            appStartedAt = appStartedAt,
            sdkVersion = sdkVersion,
            events = MockDeviceBuilder().apply(block).events.toList(),
        )
    }
}

@MockScenarioDslMarker
class MockDeviceBuilder internal constructor() {
    internal val events = mutableListOf<MockEvent>()

    fun network(
        at: Duration,
        method: String,
        url: String,
        status: Int = 200,
        duration: Duration = 100.milliseconds,
        error: String? = null,
        pending: Boolean = false,
        block: MockNetworkCallBuilder.() -> Unit = {},
    ) {
        val details = MockNetworkCallBuilder().apply(block)
        events += MockEvent.NetworkCall(
            at = at,
            method = method,
            url = url,
            status = status,
            duration = duration,
            requestHeaders = details.requestHeaders.toMap(),
            requestBody = details.requestBody,
            responseHeaders = details.responseHeaders.toMap(),
            responseBody = details.responseBody,
            contentType = details.contentType,
            error = error,
            pending = pending,
        )
    }

    fun timeline(
        at: Duration,
        value: String,
        type: String = "log",
        level: String? = "info",
        params: Map<String, String> = emptyMap(),
    ) {
        events += MockEvent.TimelineLog(at = at, value = value, type = type, level = level, params = params)
    }

    fun crash(
        at: Duration,
        exceptionType: String,
        message: String,
        stackTrace: String = "",
    ) {
        events += MockEvent.Crash(at = at, exceptionType = exceptionType, message = message, stackTrace = stackTrace)
    }

    fun analytics(
        at: Duration,
        event: String,
        vararg properties: Pair<String, String>,
        tableId: String = "analytics",
    ) {
        events += MockEvent.AnalyticsEvent(at = at, eventName = event, tableId = tableId, properties = properties.toMap())
    }

    fun featureFlags(
        provider: String,
        at: Duration = Duration.ZERO,
        block: MockFeatureFlagsBuilder.() -> Unit,
    ) {
        events += MockEvent.FeatureFlags(at = at, provider = provider, flags = MockFeatureFlagsBuilder().apply(block).flags.toList())
    }

    fun raw(
        at: Duration,
        plugin: String,
        method: String,
        body: String,
    ) {
        events += MockEvent.Raw(at = at, plugin = plugin, method = method, body = body)
    }
}

@MockScenarioDslMarker
class MockNetworkCallBuilder internal constructor() {
    internal val requestHeaders = linkedMapOf<String, String>()
    internal val responseHeaders = linkedMapOf<String, String>()
    internal var requestBody: String? = null
    internal var responseBody: String? = null
    internal var contentType: String? = null

    fun requestHeader(name: String, value: String) {
        requestHeaders[name] = value
    }

    fun responseHeader(name: String, value: String) {
        responseHeaders[name] = value
    }

    fun requestBody(body: String) {
        requestBody = body
    }

    fun responseBody(body: String) {
        responseBody = body
    }

    fun contentType(contentType: String) {
        this.contentType = contentType
    }
}

@MockScenarioDslMarker
class MockFeatureFlagsBuilder internal constructor() {
    internal val flags = mutableListOf<MockEvent.FeatureFlags.Flag>()

    fun flag(
        name: String,
        state: String,
        variant: String,
        payload: String? = null,
    ) {
        flags += MockEvent.FeatureFlags.Flag(name = name, state = state, variant = variant, payload = payload)
    }
}

private fun String.toDeviceId() = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

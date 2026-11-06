package com.flocon.data.remote.mock

import com.flocon.data.remote.analytics.model.AnalyticsItemDataModel
import com.flocon.data.remote.analytics.model.AnalyticsPropertyDataModel
import com.flocon.data.remote.crashreporter.model.CrashReportDataModel
import com.flocon.data.remote.featureflags.model.FeatureFlagDataModel
import com.flocon.data.remote.featureflags.model.FeatureFlagsDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.network.models.FloconNetworkRequestDataModel
import com.flocon.data.remote.network.models.FloconNetworkResponseDataModel
import com.flocon.data.remote.timelinelog.model.TimelineLogDataModel
import io.github.openflocon.domain.Protocol
import kotlinx.serialization.json.Json
import kotlin.time.Instant

/**
 * Turns a [MockScenario] into the messages a real SDK would send, using the same wire models
 * as the remote data sources, so the mock goes through the real decoding path.
 */
internal class MockMessageEncoder(private val json: Json) {

    fun encode(
        scenario: MockScenario,
        launchTime: Instant,
        defaultSdkVersion: String,
    ): List<FloconIncomingMessageDataModel> = scenario.devices
        .flatMap { device ->
            device.events.flatMapIndexed { index, event ->
                val timestamp = (launchTime - event.at).toEpochMilliseconds()
                encodeEvent(device, index, event, timestamp).map { (plugin, method, body) ->
                    timestamp to device.message(plugin = plugin, method = method, body = body, defaultSdkVersion = defaultSdkVersion)
                }
            }
        }
        .sortedBy { it.first } // stable: a request stays before its response
        .map { it.second }

    private fun encodeEvent(
        device: MockDevice,
        index: Int,
        event: MockEvent,
        timestamp: Long,
    ): List<Triple<String, String, String>> = when (event) {
        is MockEvent.NetworkCall -> encodeNetworkCall(callId = "mock-${device.id}-$index", call = event, timestamp = timestamp)

        is MockEvent.TimelineLog -> listOf(
            Triple(
                Protocol.FromDevice.Timeline.Plugin,
                Protocol.FromDevice.Timeline.Method.Log,
                json.encodeToString(
                    TimelineLogDataModel(
                        type = event.type,
                        value = event.value,
                        params = event.params,
                        level = event.level,
                        timestamp = timestamp,
                    ),
                ),
            ),
        )

        is MockEvent.Crash -> listOf(
            Triple(
                Protocol.FromDevice.CrashReporter.Plugin,
                Protocol.FromDevice.CrashReporter.Method.ReportCrash,
                json.encodeToString(
                    listOf(
                        CrashReportDataModel(
                            crashId = "mock-crash-${device.id}-$index",
                            timestamp = timestamp,
                            exceptionType = event.exceptionType,
                            exceptionMessage = event.message,
                            stackTrace = event.stackTrace,
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.AnalyticsEvent -> listOf(
            Triple(
                Protocol.FromDevice.Analytics.Plugin,
                Protocol.FromDevice.Analytics.Method.AddItems,
                json.encodeToString(
                    listOf(
                        AnalyticsItemDataModel(
                            id = "mock-analytics-${device.id}-$index",
                            analyticsTableId = event.tableId,
                            eventName = event.eventName,
                            createdAt = timestamp,
                            properties = event.properties.map { (name, value) -> AnalyticsPropertyDataModel(name = name, value = value) },
                        ),
                    ),
                ),
            ),
        )

        is MockEvent.FeatureFlags -> listOf(
            Triple(
                Protocol.FromDevice.FeatureFlags.Plugin,
                Protocol.FromDevice.FeatureFlags.Method.Register,
                json.encodeToString(
                    FeatureFlagsDataModel(
                        provider = event.provider,
                        flags = event.flags.map {
                            FeatureFlagDataModel(name = it.name, state = it.state, variant = it.variant, payload = it.payload)
                        },
                    ),
                ),
            ),
        )

        is MockEvent.Raw -> listOf(Triple(event.plugin, event.method, event.body))
    }

    private fun encodeNetworkCall(
        callId: String,
        call: MockEvent.NetworkCall,
        timestamp: Long,
    ): List<Triple<String, String, String>> {
        val request = Triple(
            Protocol.FromDevice.Network.Plugin,
            Protocol.FromDevice.Network.Method.LogNetworkCallRequest,
            json.encodeToString(
                FloconNetworkRequestDataModel(
                    floconCallId = callId,
                    floconNetworkType = "http",
                    url = call.url,
                    method = call.method,
                    startTime = timestamp,
                    requestHeaders = call.requestHeaders,
                    requestBody = call.requestBody,
                    requestSize = call.requestBody?.encodeToByteArray()?.size?.toLong() ?: 0L,
                    isMocked = false,
                ),
            ),
        )
        if (call.pending) return listOf(request)

        val response = Triple(
            Protocol.FromDevice.Network.Plugin,
            Protocol.FromDevice.Network.Method.LogNetworkCallResponse,
            json.encodeToString(
                FloconNetworkResponseDataModel(
                    floconCallId = callId,
                    floconNetworkType = "http",
                    durationMs = call.duration.inWholeMicroseconds / 1000.0,
                    responseHttpCode = call.status.takeIf { call.error == null },
                    responseContentType = call.contentType ?: call.responseHeaders.entries
                        .firstOrNull { it.key.equals("content-type", ignoreCase = true) }?.value,
                    responseBody = call.responseBody,
                    responseHeaders = call.responseHeaders,
                    responseSize = call.responseBody?.encodeToByteArray()?.size?.toLong() ?: 0L,
                    responseError = call.error,
                ),
            ),
        )
        return listOf(request, response)
    }

    private fun MockDevice.message(
        plugin: String,
        method: String,
        body: String,
        defaultSdkVersion: String,
    ) = FloconIncomingMessageDataModel(
        deviceName = name,
        deviceId = id,
        plugin = plugin,
        body = body,
        method = method,
        appName = appName,
        appPackageName = packageName,
        appInstance = appInstance,
        platform = platform,
        versionName = sdkVersion ?: defaultSdkVersion,
    )
}

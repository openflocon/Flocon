package com.flocon.data.remote.mock

import com.flocon.data.remote.analytics.datasource.AnalyticsRemoteDataSourceImpl
import com.flocon.data.remote.crashreporter.datasource.CrashReportRemoteDataSourceImpl
import com.flocon.data.remote.featureflags.datasource.FeatureFlagsRemoteDataSourceImpl
import com.flocon.data.remote.messages.mapper.toDomain
import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.network.datasource.NetworkRemoteDataSourceImpl
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.timelinelog.datasource.TimelineLogRemoteDataSourceImpl
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.featureflags.models.FeatureFlagStateDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class MockMessageEncoderTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val encoder = MockMessageEncoder(json)
    private val launch = Instant.fromEpochMilliseconds(1_800_000_000_000)
    private val desktopVersion = "9.9.9"

    private fun encode(block: MockDeviceBuilder.() -> Unit) = encoder.encode(
        scenario = scenario("s") { android("Pixel 8", app = "App", packageName = "a.b", block = block) },
        launchTime = launch,
        defaultSdkVersion = desktopVersion,
    )

    private fun epochMsBefore(duration: kotlin.time.Duration) = (launch - duration).toEpochMilliseconds()

    @Test
    fun `network call round-trips`() {
        val messages = encode {
            network(5.minutes.ago, "GET", "https://api.example.com/users", status = 200, duration = 120.milliseconds) {
                responseBody("[]")
            }
        }

        assertEquals(2, messages.size)
        val (request, response) = messages
        assertEquals(Protocol.FromDevice.Network.Plugin, request.plugin)
        assertEquals(Protocol.FromDevice.Network.Method.LogNetworkCallRequest, request.method)
        assertEquals(Protocol.FromDevice.Network.Method.LogNetworkCallResponse, response.method)

        val network = NetworkRemoteDataSourceImpl(NoServer, json)
        val call = assertNotNull(network.getRequestData(request.toDomain()))
        assertEquals("https://api.example.com/users", call.request.url)
        assertEquals("GET", call.request.method)
        assertEquals(epochMsBefore(5.minutes), call.request.startTime)

        val responseData = assertNotNull(network.getResponseData(response.toDomain()))
        assertEquals(call.callId, responseData.floconCallId)
        val success = assertIs<FloconNetworkCallDomainModel.Response.Success>(responseData.response)
        assertEquals(120.0, success.durationMs)
        assertEquals("[]", success.body)
        val http = assertIs<FloconNetworkCallDomainModel.Response.Success.SpecificInfos.Http>(success.specificInfos)
        assertEquals(200, http.httpCode)
    }

    @Test
    fun `network error round-trips`() {
        val messages = encode {
            network(1.minutes.ago, "GET", "https://x", error = "timeout")
        }

        val responseData = assertNotNull(NetworkRemoteDataSourceImpl(NoServer, json).getResponseData(messages[1].toDomain()))
        val failure = assertIs<FloconNetworkCallDomainModel.Response.Failure>(responseData.response)
        assertEquals("timeout", failure.issue)
    }

    @Test
    fun `pending call emits request only`() {
        val messages = encode {
            network(1.minutes.ago, "GET", "https://x", pending = true)
        }

        assertEquals(Protocol.FromDevice.Network.Method.LogNetworkCallRequest, messages.single().method)
    }

    @Test
    fun `timeline round-trips`() {
        val message = encode {
            timeline(6.minutes.ago, "CheckoutScreen", type = "navigation", level = "warning", params = mapOf("from" to "Cart"))
        }.single()

        assertEquals(Protocol.FromDevice.Timeline.Plugin, message.plugin)
        val log = assertNotNull(TimelineLogRemoteDataSourceImpl(json).getLog(message.toDomain()))
        assertEquals("navigation", log.type)
        assertEquals("CheckoutScreen", log.value)
        assertEquals(TimelineLogLevel.Warning, log.level)
        assertEquals(mapOf("from" to "Cart"), log.params)
        assertEquals(epochMsBefore(6.minutes), log.timestamp)
    }

    @Test
    fun `crash round-trips`() {
        val message = encode {
            crash(2.minutes.ago, exceptionType = "java.lang.IllegalStateException", message = "boom", stackTrace = "at a.b")
        }.single()

        assertEquals(Protocol.FromDevice.CrashReporter.Plugin, message.plugin)
        val crash = CrashReportRemoteDataSourceImpl(json).getItems(message.toDomain()).single()
        assertEquals("java.lang.IllegalStateException", crash.exceptionType)
        assertEquals("boom", crash.exceptionMessage)
        assertEquals("at a.b", crash.stackTrace)
        assertEquals(epochMsBefore(2.minutes), crash.timestamp)
    }

    @Test
    fun `analytics round-trips`() {
        val message = encode {
            analytics(1.minutes.ago, "screen_view", "screen" to "home")
        }.single()

        assertEquals(Protocol.FromDevice.Analytics.Plugin, message.plugin)
        val item = AnalyticsRemoteDataSourceImpl(json).getItems(message.toDomain()).single()
        assertEquals("screen_view", item.eventName)
        assertEquals("analytics", item.analyticsTableId)
        assertEquals(epochMsBefore(1.minutes), item.createdAt)
        assertEquals(listOf("screen" to "home"), item.properties.map { it.name to it.value })
    }

    @Test
    fun `feature flags round-trip`() {
        val message = encode {
            featureFlags("firebase") {
                flag("new_checkout", state = "custom", variant = "treatment", payload = "{}")
                flag("dark_mode", state = "on", variant = "on")
            }
        }.single()

        assertEquals(Protocol.FromDevice.FeatureFlags.Plugin, message.plugin)
        val provider = assertNotNull(FeatureFlagsRemoteDataSourceImpl(json).getFeatureFlags(message.toDomain()))
        assertEquals("firebase", provider.provider)
        assertEquals(listOf("new_checkout", "dark_mode"), provider.flags.map { it.name })
        assertEquals(FeatureFlagStateDomainModel.On, provider.flags[1].state)
        assertEquals("{}", provider.flags[0].payload)
    }

    @Test
    fun `raw body is passed through untouched`() {
        val message = encode {
            raw(30.seconds.ago, plugin = "network", method = "log_network_call_request", body = "{not json")
        }.single()

        assertEquals("network", message.plugin)
        assertEquals("log_network_call_request", message.method)
        assertEquals("{not json", message.body)
    }

    @Test
    fun `messages are sorted by time`() {
        val messages = encode {
            timeline(1.minutes.ago, "one")
            timeline(5.minutes.ago, "five")
            timeline(3.minutes.ago, "three")
        }

        val values = messages.map { TimelineLogRemoteDataSourceImpl(json).getLog(it.toDomain())!!.value }
        assertEquals(listOf("five", "three", "one"), values)
    }

    @Test
    fun `each device gets its own envelope`() {
        val messages = encoder.encode(
            scenario = scenario("s") {
                android("Pixel 8", app = "App", packageName = "a.b") { timeline(2.minutes.ago, "android") }
                ios("iPhone 16", app = "Other", packageName = "c.d") { timeline(1.minutes.ago, "ios") }
            },
            launchTime = launch,
            defaultSdkVersion = desktopVersion,
        )

        val (android, ios) = messages
        assertEquals(listOf("pixel-8", "Pixel 8", "App", "a.b", "android", epochMsBefore(2.minutes + 1.seconds)), android.envelope())
        assertEquals(listOf("iphone-16", "iPhone 16", "Other", "c.d", "ios", epochMsBefore(1.minutes + 1.seconds)), ios.envelope())
    }

    @Test
    fun `appInstance is the app start time`() {
        val messages = encoder.encode(
            scenario = scenario("s") {
                android("Pixel 8", app = "App", packageName = "a.b", appStartedAt = 20.minutes.ago) { timeline(2.minutes.ago, "explicit") }
                android("Pixel 4", app = "App", packageName = "a.b") {
                    timeline(3.minutes.ago, "earliest")
                    timeline(1.minutes.ago, "latest")
                }
                android("Pixel 3", app = "App", packageName = "a.b") { featureFlags("p") {} }
            },
            launchTime = launch,
            defaultSdkVersion = desktopVersion,
        )

        val byDevice = messages.groupBy { it.deviceId }.mapValues { (_, it) -> it.map { m -> m.appInstance }.distinct().single() }
        assertEquals(epochMsBefore(20.minutes), byDevice["pixel-8"]) // given
        assertEquals(epochMsBefore(3.minutes + 1.seconds), byDevice["pixel-4"]) // just before the earliest event
        assertEquals(epochMsBefore(1.seconds), byDevice["pixel-3"]) // events at launch: started just before
    }

    @Test
    fun `sdk version defaults to desktop version`() {
        val messages = encoder.encode(
            scenario = scenario("s") {
                android("Pixel 8", app = "App", packageName = "a.b") { timeline(2.minutes.ago, "new") }
                android("Pixel 4", app = "App", packageName = "a.b", sdkVersion = "1.0.0") { timeline(1.minutes.ago, "old") }
            },
            launchTime = launch,
            defaultSdkVersion = desktopVersion,
        )

        assertEquals(listOf(desktopVersion, "1.0.0"), messages.map { it.versionName })
    }

    private fun FloconIncomingMessageDataModel.envelope() = listOf(deviceId, deviceName, appName, appPackageName, platform, appInstance)
}

private object NoServer : Server {
    override fun startWebsocket(port: Int) = Unit
    override fun starHttp(port: Int) = Unit
    override fun stop() = Unit
    override val receivedMessages: Flow<FloconIncomingMessageDataModel> = emptyFlow()
    override suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    ) = Unit
    override val activeDevices: Flow<Set<FloconDeviceIdAndPackageNameDataModel>> = emptyFlow()
    override val receivedFiles: Flow<FloconReceivedFileDomainModel> = emptyFlow()
}

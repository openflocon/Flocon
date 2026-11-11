package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceConnectionTelemetryTest {

    private class FakeClient : TelemetryClient {
        val events = mutableListOf<Pair<String, Map<String, Any>>>()
        override suspend fun capture(event: String, distinctId: String, properties: Map<String, Any>) {
            events += event to properties
        }
    }

    private class FakeRemoteDeviceDataSource(
        private val sdk: String?,
    ) : RemoteDeviceDataSource {
        override val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> = emptyFlow()
        override fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String? = null
        override fun getInstalledPlugins(message: FloconIncomingMessageDomainModel): Set<String>? = null
        override fun getConnectionType(message: FloconIncomingMessageDomainModel) = DeviceConnectionType.Wifi
        override fun getSdk(message: FloconIncomingMessageDomainModel): String? = sdk
        override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) = Unit
    }

    private class FakePreferences : TelemetryPreferences {
        override var level: TelemetryLevel? = TelemetryLevel.Anonymous
        override var installationId: String? = null
    }

    private fun telemetry(client: TelemetryClient, sdk: String? = "kotlin") = DeviceConnectionTelemetry(
        remoteDeviceDataSource = FakeRemoteDeviceDataSource(sdk),
        telemetryManager = TelemetryManager(
            preferences = FakePreferences(),
            client = client,
            applicationScope = CoroutineScope(Dispatchers.Unconfined),
            config = TelemetryConfig(apiKey = "phc_test", host = "https://eu.i.posthog.com", appVersion = "1.0.0", doNotTrack = false),
        ),
    )

    private fun bonjour(appInstance: Long) = FloconIncomingMessageDomainModel(
        deviceName = "Pixel 9 of someone",
        deviceId = "pixel",
        plugin = "bonjour",
        body = "",
        method = "bonjour",
        appName = "Secret app",
        appPackageName = "com.secret.app",
        appInstance = appInstance,
        platform = "android",
        floconVersionOnDevice = "1.4.0",
    )

    private fun FloconIncomingMessageDomainModel.id() = DeviceIdAndPackageNameDomainModel(
        deviceId = deviceId,
        packageName = appPackageName,
        appInstance = appInstance,
    )

    @Test
    fun `one event per app launch, even when the sdk reconnects`() = runBlocking {
        val client = FakeClient()
        val telemetry = telemetry(client)
        val firstLaunch = bonjour(appInstance = 1)
        val secondLaunch = bonjour(appInstance = 2)

        telemetry.onMessageReceived(firstLaunch.id(), firstLaunch)
        telemetry.onMessageReceived(firstLaunch.id(), firstLaunch) // reconnection
        telemetry.onMessageReceived(secondLaunch.id(), secondLaunch)

        assertEquals(2, client.events.size)
        val (event, properties) = client.events.first()
        assertEquals(TelemetryEvents.DEVICE_CONNECTED, event)
        assertEquals("android", properties["platform"])
        assertEquals("kotlin", properties["sdk"])
        assertEquals("1.4.0", properties["sdk_version"])
        assertEquals("wifi", properties["connection_type"])
        // nothing identifying the device or the app
        val values = properties.values.map { it.toString() }
        listOf("pixel", "Pixel 9 of someone", "Secret app", "com.secret.app").forEach {
            assertEquals(false, it in values, "$it must not be sent")
        }
    }

    @Test
    fun `values sent by the sdk are restricted to known ones`() {
        val properties = deviceConnectedProperties(
            platform = "toaster",
            sdk = "my-own-sdk",
            sdkVersion = "https://example.com/secret",
            connectionType = null,
        )
        assertEquals("other", properties["platform"])
        assertEquals("other", properties["sdk"])
        assertEquals("unknown", properties["sdk_version"])
        assertEquals("unknown", properties["connection_type"])

        assertEquals("unknown", deviceConnectedProperties("ios", null, "2.0.0-beta.1", DeviceConnectionType.Default)["sdk"])
        assertEquals("2.0.0-beta.1", deviceConnectedProperties("ios", null, "2.0.0-beta.1", DeviceConnectionType.Default)["sdk_version"])
    }
}

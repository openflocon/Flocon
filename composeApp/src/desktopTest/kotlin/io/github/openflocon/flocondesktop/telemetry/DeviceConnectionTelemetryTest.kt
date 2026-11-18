package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceConnectionTelemetryTest {

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

    private fun telemetry(client: FakeTelemetryRepository, sdk: String? = "kotlin") = DeviceConnectionTelemetry(
        remoteDeviceDataSource = FakeRemoteDeviceDataSource(sdk),
        telemetryManager = telemetryManager(client),
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
    fun `one event per device and app per Flocon launch, whatever the app instance`() = runBlocking {
        val client = FakeTelemetryRepository()
        val telemetry = telemetry(client)
        val firstLaunch = bonjour(appInstance = 1)
        val secondLaunch = bonjour(appInstance = 2)

        telemetry.onMessageReceived(firstLaunch.id(), firstLaunch)
        telemetry.onMessageReceived(firstLaunch.id(), firstLaunch) // reconnection
        telemetry.onMessageReceived(secondLaunch.id(), secondLaunch)

        // the second app launch (rebuild) does not send again
        assertEquals(1, client.events.size)
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
    fun `another app or another device is sent`() = runBlocking {
        val client = FakeTelemetryRepository()
        val telemetry = telemetry(client)
        val first = bonjour(appInstance = 1)
        val otherApp = first.copy(appPackageName = "com.other.app")
        val otherDevice = first.copy(deviceId = "iphone")

        listOf(first, otherApp, otherDevice, first).forEach { telemetry.onMessageReceived(it.id(), it) }

        assertEquals(3, client.events.size)
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

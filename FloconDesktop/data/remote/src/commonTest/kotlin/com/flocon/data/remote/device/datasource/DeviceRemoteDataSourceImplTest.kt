package com.flocon.data.remote.device.datasource

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeviceRemoteDataSourceImplTest {

    private val server = object : Server {
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

    private val dataSource = DeviceRemoteDataSourceImpl(Json { ignoreUnknownKeys = true }, server)

    private fun bonjour(body: String) = FloconIncomingMessageDomainModel(
        deviceName = "Pixel 9",
        deviceId = "pixel",
        plugin = "bonjour",
        body = body,
        method = "bonjour",
        appName = "Sample",
        appPackageName = "io.flocon.sample",
        appInstance = 1L,
        platform = "android",
        floconVersionOnDevice = "1.0.0",
    )

    @Test
    fun `the connection type is read from the bonjour message`() {
        assertEquals(
            DeviceConnectionType.Wifi,
            dataSource.getConnectionType(bonjour("""{"plugins":["network"],"connectionType":"wifi"}""")),
        )
        assertEquals(
            DeviceConnectionType.Default,
            dataSource.getConnectionType(bonjour("""{"plugins":["network"],"connectionType":"default"}""")),
        )
    }

    @Test
    fun `an unreadable bonjour message has no connection type`() {
        assertNull(dataSource.getConnectionType(bonjour("bonjour")))
    }
}

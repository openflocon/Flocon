package com.flocon.data.remote.mock

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Replaces the websocket server in mock mode (`--mock=<name>`): the scenario devices look connected
 * and their messages go through the same handlers as a real device. No port is opened.
 */
class MockServer(
    scenario: MockScenario,
    json: Json,
    defaultSdkVersion: String,
    launchTime: Instant = Clock.System.now(),
) : Server {

    private val messages = MockMessageEncoder(json).encode(scenario, launchTime, defaultSdkVersion)

    // the server is restarted regularly (MessagesServerDelegate): the scenario must be replayed once only
    private val alreadyEmitted = MutableStateFlow(false)

    override val receivedMessages: Flow<FloconIncomingMessageDataModel> = flow {
        if (alreadyEmitted.compareAndSet(expect = false, update = true)) {
            messages.forEach { emit(it) }
        }
        awaitCancellation()
    }

    override val activeDevices: StateFlow<Set<FloconDeviceIdAndPackageNameDataModel>> = MutableStateFlow(
        scenario.devices.map {
            FloconDeviceIdAndPackageNameDataModel(deviceId = it.id, packageName = it.packageName, appInstance = it.appInstance(launchTime))
        }.toSet(),
    )

    override val receivedFiles: Flow<FloconReceivedFileDomainModel> = emptyFlow()

    override fun startWebsocket(port: Int) = Unit

    override fun starHttp(port: Int) = Unit

    override fun stop() = Unit

    override suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    ) = Unit // the mocked devices ignore the commands of the desktop
}

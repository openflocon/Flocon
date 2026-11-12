package com.flocon.data.remote.mock

import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel
import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.server.Server
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Replaces the websocket server in mock mode (`--mock=<name>`): the scenario devices look connected
 * and their messages go through the same handlers as a real device. No port is opened.
 */
class MockServer(
    private val scenario: MockScenario,
    private val json: Json,
    private val defaultSdkVersion: String,
    private val launchTime: Instant = Clock.System.now(),
) : Server {

    private val encoder = MockMessageEncoder(json)
    private val messages = encoder.encode(scenario, launchTime, defaultSdkVersion)

    // the answers of the mocked devices to the requests of the desktop (SQL queries, file listing, preferences...)
    private val replies = Channel<FloconIncomingMessageDataModel>(Channel.UNLIMITED)
    private val uploadedFiles = Channel<FloconReceivedFileDomainModel>(Channel.UNLIMITED)
    private val responders = scenario.devices.associate { it.id to it }.mapValues { (_, device) -> MockDeviceResponder(device, launchTime, json) }

    // the server is restarted regularly (MessagesServerDelegate): the scenario must be replayed once only
    private val alreadyEmitted = MutableStateFlow(false)

    override val receivedMessages: Flow<FloconIncomingMessageDataModel> = flow {
        if (alreadyEmitted.compareAndSet(expect = false, update = true)) {
            messages.forEach { emit(it) }
        }
        emitAll(replies.receiveAsFlow())
    }

    override val activeDevices: StateFlow<Set<FloconDeviceIdAndPackageNameDataModel>> = MutableStateFlow(
        scenario.devices.map {
            FloconDeviceIdAndPackageNameDataModel(deviceId = it.id, packageName = it.packageName, appInstance = it.appInstance(launchTime))
        }.toSet(),
    )

    override val receivedFiles: Flow<FloconReceivedFileDomainModel> = uploadedFiles.receiveAsFlow()

    override fun startWebsocket(port: Int) = Unit

    override fun starHttp(port: Int) = Unit

    override fun stop() = Unit

    override suspend fun sendMessageToClient(
        deviceIdAndPackageName: FloconDeviceIdAndPackageNameDataModel,
        message: FloconOutgoingMessageDataModel,
    ) {
        // the mocked devices answer the requests they have data for (databases, files, preferences), and ignore the other commands
        val device = scenario.devices.firstOrNull { it.id == deviceIdAndPackageName.deviceId && it.packageName == deviceIdAndPackageName.packageName } ?: return
        val responder = responders[device.id] ?: return
        val appInstance = device.appInstance(launchTime)
        responder.respond(message).forEach { reply ->
            when (reply) {
                is MockReply.Message -> replies.trySend(
                    with(encoder) {
                        device.message(plugin = reply.plugin, method = reply.method, body = reply.body, appInstance = appInstance, defaultSdkVersion = defaultSdkVersion)
                    },
                )

                is MockReply.UploadedFile -> uploadedFiles.trySend(
                    FloconReceivedFileDomainModel(
                        deviceId = device.id,
                        remotePath = reply.remotePath,
                        localPath = reply.localPath,
                        requestId = reply.requestId,
                        appPackageName = device.packageName,
                        appInstance = appInstance,
                    ),
                )
            }
        }
    }
}

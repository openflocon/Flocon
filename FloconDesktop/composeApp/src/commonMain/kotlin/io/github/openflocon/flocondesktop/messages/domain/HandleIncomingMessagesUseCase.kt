package io.github.openflocon.flocondesktop.messages.domain

import io.github.openflocon.flocondesktop.FloconIncomingMessageDataModel
import io.github.openflocon.flocondesktop.core.domain.device.HandleDeviceUseCase
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceAppDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceDomainModel
import io.github.openflocon.flocondesktop.messages.domain.repository.MessagesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HandleIncomingMessagesUseCase(
    private val messagesRepository: MessagesRepository,
    private val plugins: List<MessagesReceiverRepository>,
    private val handleDeviceUseCase: HandleDeviceUseCase,
) {

    operator fun invoke(): Flow<Unit> = messagesRepository
        .listenMessages()
        .onEach {
            val deviceId = handleDeviceUseCase(device = getDevice(it))
            plugins.forEach { plugin ->
                if (plugin.pluginName.contains(it.plugin)) {
                    plugin.onMessageReceived(deviceId = deviceId, message = it)
                }
            }
        }
        .map { }

    private fun getDevice(message: FloconIncomingMessageDataModel): DeviceDomainModel = DeviceDomainModel(
        deviceName = message.deviceName,
        deviceId = message.deviceId,
        apps = listOf(
            DeviceAppDomainModel(
                name = message.appName,
                packageName = message.appPackageName
            )
        )
    )
}

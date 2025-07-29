package com.florent37.flocondesktop.messages.domain

import com.florent37.flocondesktop.FloconIncomingMessageDataModel
import com.florent37.flocondesktop.core.domain.device.HandleDeviceUseCase
import com.florent37.flocondesktop.messages.domain.model.DeviceDomainModel
import com.florent37.flocondesktop.messages.domain.repository.MessagesRepository
import com.florent37.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
import kotlinx.coroutines.flow.Flow
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
        }.map { }

    private fun getDevice(message: FloconIncomingMessageDataModel): DeviceDomainModel = DeviceDomainModel(
        appName = message.appName,
        deviceName = message.deviceName,
        appPackageName = message.appPackageName,
        deviceId = message.id,
    )
}

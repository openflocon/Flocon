package io.github.openflocon.flocondesktop.messages.domain

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.domain.device.usecase.HandleDeviceUseCase
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.messages.domain.repository.MessagesRepository
import io.github.openflocon.flocondesktop.messages.domain.repository.sub.MessagesReceiverRepository
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
            val handleDeviceResult = handleDeviceUseCase(device = getDevice(it))
            plugins.forEach { plugin ->
                if (handleDeviceResult.isNewDevice) {
                    plugin.onNewDevice(
                        deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                            deviceId = handleDeviceResult.deviceId,
                            packageName = it.appPackageName,
                        )
                    )
                }
                if (plugin.pluginName.contains(it.plugin)) {
                    plugin.onMessageReceived(deviceId = handleDeviceResult.deviceId, message = it)
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
                packageName = message.appPackageName,
            ),
        ),
    )
}

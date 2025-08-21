package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.HandleDeviceUseCase
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.messages.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HandleIncomingMessagesUseCase(
    private val messagesRepository: MessagesRepository,
    private val plugins: List<MessagesReceiverRepository>,
    private val handleDeviceUseCase: HandleDeviceUseCase,
    private val handleNewDeviceUseCase: HandleNewDeviceUseCase,
) {

    operator fun invoke(): Flow<Unit> = messagesRepository
        .listenMessages()
        .onEach {
            val handleDeviceResult = handleDeviceUseCase(device = getDevice(it))
            if (handleDeviceResult.isNewDevice) {
                handleNewDeviceUseCase(
                    deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                        deviceId = handleDeviceResult.deviceId,
                        packageName = it.appPackageName,
                    )
                )
            }
            plugins.forEach { plugin ->
                if (handleDeviceResult.isNewDevice) {
                    plugin.onDeviceConnected(
                        deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                            deviceId = handleDeviceResult.deviceId,
                            packageName = it.appPackageName,
                        ),
                        isNewDevice = true, // TODO on a next MR, for now handleDeviceResult.isNewDevice is always true the first time
                    )
                }
                if (plugin.pluginName.contains(it.plugin)) {
                    plugin.onMessageReceived(deviceId = handleDeviceResult.deviceId, message = it)
                }
            }
        }
        .map { }

    private fun getDevice(message: FloconIncomingMessageDomainModel): DeviceDomainModel = DeviceDomainModel(
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

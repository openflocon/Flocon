package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.usecase.HandleDeviceAndAppUseCase
import io.github.openflocon.domain.device.usecase.HandleNewAppUseCase
import io.github.openflocon.domain.device.usecase.HandleNewDeviceUseCase
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import io.github.openflocon.domain.messages.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HandleIncomingMessagesUseCase(
    private val messagesRepository: MessagesRepository,
    private val plugins: List<MessagesReceiverRepository>,
    private val handleDeviceAndAppUseCase: HandleDeviceAndAppUseCase,
    private val handleNewDeviceUseCase: HandleNewDeviceUseCase,
    private val handleNewAppUseCase: HandleNewAppUseCase,
) {

    operator fun invoke(): Flow<Unit> = messagesRepository
        .listenMessages()
        .onEach { message ->
            val handleDeviceResult = handleDeviceAndAppUseCase(device = getDeviceAndApp(message))

            val deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                deviceId = handleDeviceResult.deviceId,
                packageName = message.appPackageName,
            )

            if (handleDeviceResult.isNewDevice) {
                handleNewDeviceUseCase(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                )
            }
            if (handleDeviceResult.isNewApp) {
                handleNewAppUseCase(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                )
            }
            plugins.forEach { plugin ->
                if (handleDeviceResult.justConnectedForThisSession) {
                    plugin.onDeviceConnected(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        isNewDevice = handleDeviceResult.isNewDevice,
                    )
                }
                if (plugin.pluginName.contains(message.plugin)) {
                    plugin.onMessageReceived(
                        deviceIdAndPackageName = deviceIdAndPackageName,
                        message = message
                    )
                }
            }
        }
        .map { }

    private fun getDeviceAndApp(message: FloconIncomingMessageDomainModel) =
        RegisterDeviceWithAppDomainModel(
            device = DeviceDomainModel(
                deviceId = message.deviceId,
                message.deviceName,
            ),
            app = DeviceAppDomainModel(
                name = message.appName,
                packageName = message.appPackageName,
                iconEncoded = null,
            ),
        )
}

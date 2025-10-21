package io.github.openflocon.domain.messages.usecase

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.FileReceiverRepository
import io.github.openflocon.domain.messages.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HandleReceivedFilesUseCase(
    private val messagesRepository: MessagesRepository,
    private val plugins: List<FileReceiverRepository>,
) {

    operator fun invoke(): Flow<Unit> = messagesRepository
        .listenReceivedFiles()
        .onEach { receivedFile ->
            val deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
                deviceId = receivedFile.deviceId,
                packageName = receivedFile.appPackageName,
                appInstance = receivedFile.appInstance,
            )

            plugins.forEach { plugin ->
                plugin.onFileReceived(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    receivedFile = receivedFile
                )
            }
        }
        .map { }

    private fun getDeviceAndApp(message: FloconIncomingMessageDomainModel) =
        RegisterDeviceWithAppDomainModel(
            device = DeviceDomainModel(
                deviceId = message.deviceId,
                message.deviceName,
                platform = message.platform,
            ),
            app = DeviceAppDomainModel(
                name = message.appName,
                packageName = message.appPackageName,
                lastAppInstance = message.appInstance,
                iconEncoded = null,
            ),
        )
}

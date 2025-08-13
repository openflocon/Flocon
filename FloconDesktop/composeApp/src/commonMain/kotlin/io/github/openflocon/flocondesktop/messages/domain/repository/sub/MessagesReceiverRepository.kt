package io.github.openflocon.flocondesktop.messages.domain.repository.sub

import com.flocon.data.remote.models.FloconIncomingMessageDataModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel


interface MessagesReceiverRepository {
    val pluginName: List<String>
    suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDataModel,
    )

    suspend fun onNewDevice(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

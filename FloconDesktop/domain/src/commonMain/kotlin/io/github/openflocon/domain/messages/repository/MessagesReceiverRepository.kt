package io.github.openflocon.domain.messages.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface MessagesReceiverRepository {
    val pluginName: List<String>

    suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDomainModel,
    )

    suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    )
}

package com.flocon.data.remote.device.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.database.models.DatabaseOutgoingQueryMessage
import com.flocon.data.remote.device.model.RegisterDeviceDataModel
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class DeviceRemoteDataSourceImpl(
    private val json: Json,
    private val server: Server,
) : RemoteDeviceDataSource {

    override fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String? = json.safeDecodeFromString<RegisterDeviceDataModel>(message.body)
        ?.serial

    override suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Device.Plugin,
                method = Protocol.ToDevice.Device.Method.GetAppIcon,
                body = ""
            ),
        )
    }
}

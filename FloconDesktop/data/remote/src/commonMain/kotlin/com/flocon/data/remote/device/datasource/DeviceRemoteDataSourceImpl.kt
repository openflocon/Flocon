package com.flocon.data.remote.device.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.device.model.RegisterDeviceDataModel
import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class DeviceRemoteDataSourceImpl(
    private val json: Json,
) : RemoteDeviceDataSource {

    override fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String? = json.safeDecodeFromString<RegisterDeviceDataModel>(message.body)
        ?.serial
}

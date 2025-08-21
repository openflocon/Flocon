package io.github.openflocon.data.core.device.datasource.remote

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface RemoteDeviceDataSource {
    fun getDeviceSerial(message: FloconIncomingMessageDomainModel) : String?
    suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

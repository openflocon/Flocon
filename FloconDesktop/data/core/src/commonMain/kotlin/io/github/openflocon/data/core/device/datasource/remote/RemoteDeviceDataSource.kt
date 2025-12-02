package io.github.openflocon.data.core.device.datasource.remote

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow

interface RemoteDeviceDataSource {
    val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> // devices with active websocket connection

    fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String?
    suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun restartApp(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

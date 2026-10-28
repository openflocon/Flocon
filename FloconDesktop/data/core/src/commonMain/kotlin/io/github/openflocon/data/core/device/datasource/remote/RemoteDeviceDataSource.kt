package io.github.openflocon.data.core.device.datasource.remote

import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow

interface RemoteDeviceDataSource {
    val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> // devices with active websocket connection

    fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String?

    // null when the message does not list the plugins (older SDK)
    fun getInstalledPlugins(message: FloconIncomingMessageDomainModel): Set<String>?

    // null when the message cannot be read
    fun getConnectionType(message: FloconIncomingMessageDomainModel): DeviceConnectionType?

    suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun restartApp(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

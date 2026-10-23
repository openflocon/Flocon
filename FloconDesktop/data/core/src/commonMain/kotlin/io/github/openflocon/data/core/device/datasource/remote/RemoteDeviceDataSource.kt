package io.github.openflocon.data.core.device.datasource.remote

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.flow.Flow

interface RemoteDeviceDataSource {
    val activeDevices: Flow<Set<DeviceIdAndPackageNameDomainModel>> // devices with active websocket connection

    val activeAddresses: Flow<Map<DeviceId, String>> // remote host of the active websocket of each device

    fun getDeviceSerial(message: FloconIncomingMessageDomainModel): String?

    // null when the message does not list the plugins (older SDK)
    fun getInstalledPlugins(message: FloconIncomingMessageDomainModel): Set<String>?
    suspend fun askForDeviceAppIcon(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun restartApp(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

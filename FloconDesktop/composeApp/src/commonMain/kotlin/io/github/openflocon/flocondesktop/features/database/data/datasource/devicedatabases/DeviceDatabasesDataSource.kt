package io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import io.github.openflocon.flocondesktop.messages.domain.model.toFlocon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDatabasesDataSource(
    private val server: Server,
) {
    private val deviceDatabases = MutableStateFlow<Map<DeviceIdAndPackageName, List<DeviceDataBaseDomainModel>>>(emptyMap())
    private val selectedDeviceDatabases = MutableStateFlow<Map<DeviceIdAndPackageName, DeviceDataBaseDomainModel?>>(emptyMap())

    fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<DeviceDataBaseDomainModel?> = selectedDeviceDatabases
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    fun selectDeviceDatabase(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
    ) {
        val deviceDatabaseList = deviceDatabases.value[deviceIdAndPackageName] ?: return
        val database = deviceDatabaseList.firstOrNull { it.id == databaseId } ?: return

        selectedDeviceDatabases.update { it + (deviceIdAndPackageName to database) }
    }

    fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName): Flow<List<DeviceDataBaseDomainModel>> =
        deviceDatabases.map { it[deviceIdAndPackageName] ?: emptyList() }

    fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databases: List<DeviceDataBaseDomainModel>,
    ) {
        deviceDatabases.update {
            val actual = it[deviceIdAndPackageName]
            val newList =
                buildList<DeviceDataBaseDomainModel> {
                    actual?.let { addAll(it) }
                    addAll(databases)
                }.distinct()
            it + (deviceIdAndPackageName to newList)
        }

        if (databases.isNotEmpty()) {
            // select the first db if no one for this device id
            selectedDeviceDatabases.update { state ->
                val dbForThisDevice = state[deviceIdAndPackageName]
                if (dbForThisDevice == null) {
                    state + (deviceIdAndPackageName to databases.first())
                } else {
                    state
                }
            }
        }
    }

    suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageName) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toFlocon(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Database.Plugin,
                method = Protocol.ToDevice.Database.Method.GetDatabases,
                body = "",
            ),
        )
    }
}

package io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

class DeviceDatabasesDataSource(
    private val server: Server,
) {
    private val deviceDatabases =
        MutableStateFlow<Map<DeviceId, List<DeviceDataBaseDomainModel>>>(emptyMap())

    private val selectedDeviceDatabases =
        MutableStateFlow<Map<DeviceId, DeviceDataBaseDomainModel?>>(emptyMap())

    fun observeSelectedDeviceDatabase(deviceId: DeviceId): Flow<DeviceDataBaseDomainModel?> = selectedDeviceDatabases
        .map {
            it[deviceId]
        }.distinctUntilChanged()

    fun selectDeviceDatabase(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
    ) {
        val deviceDatabaseList = deviceDatabases.value[deviceId] ?: return
        val database = deviceDatabaseList.firstOrNull { it.id == databaseId } ?: return

        selectedDeviceDatabases.update {
            it + (deviceId to database)
        }
    }

    fun observeDeviceDatabases(deviceId: DeviceId): Flow<List<DeviceDataBaseDomainModel>> = deviceDatabases.map { it[deviceId] ?: emptyList() }

    fun registerDeviceDatabases(
        deviceId: DeviceId,
        databases: List<DeviceDataBaseDomainModel>,
    ) {
        deviceDatabases.update {
            val actual = it[deviceId]
            val newList =
                buildList<DeviceDataBaseDomainModel> {
                    actual?.let { addAll(it) }
                    addAll(databases)
                }.distinct()
            it + (deviceId to newList)
        }

        if (databases.isNotEmpty()) {
            // select the first db if no one for this device id
            selectedDeviceDatabases.update { state ->
                val dbForThisDevice = state[deviceId]
                if (dbForThisDevice == null) {
                    state + (deviceId to databases.first())
                } else {
                    state
                }
            }
        }
    }

    suspend fun askForDeviceDatabases(deviceId: DeviceId) {
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Database.Plugin,
                method = Protocol.ToDevice.Database.Method.GetDatabases,
                body = "",
            ),
        )
    }
}

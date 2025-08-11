package com.flocon.data.remote.database.datasource

import com.flocon.data.remote.Protocol
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.database.models.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDatabasesRemoteDataSourceImpl(
    private val server: Server,
) : DeviceDatabasesRemoteDataSource {
    private val deviceDatabases = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, List<DeviceDataBaseDomainModel>>>(emptyMap())
    private val selectedDeviceDatabases = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, DeviceDataBaseDomainModel?>>(emptyMap())

    override fun observeSelectedDeviceDatabase(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<DeviceDataBaseDomainModel?> =
        selectedDeviceDatabases
            .map { it[deviceIdAndPackageName] }
            .distinctUntilChanged()

    override fun selectDeviceDatabase(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
    ) {
        val deviceDatabaseList = deviceDatabases.value[deviceIdAndPackageName] ?: return
        val database = deviceDatabaseList.firstOrNull { it.id == databaseId } ?: return

        selectedDeviceDatabases.update { it + (deviceIdAndPackageName to database) }
    }

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>> =
        deviceDatabases.map { it[deviceIdAndPackageName] ?: emptyList() }

    override fun registerDeviceDatabases(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
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

    override suspend fun askForDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Database.Plugin,
                method = Protocol.ToDevice.Database.Method.GetDatabases,
                body = "",
            ),
        )
    }
}

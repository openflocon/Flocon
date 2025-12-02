package com.flocon.data.remote.database.datasource

import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import io.github.openflocon.data.core.database.datasource.DeviceDatabasesRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DeviceDatabasesRemoteDataSourceImpl(
    private val server: Server,
) : DeviceDatabasesRemoteDataSource {
    private val deviceDatabases = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, List<DeviceDataBaseDomainModel>>>(emptyMap())

    override suspend fun getDatabaseById(databaseId: String): DeviceDataBaseDomainModel? {
        deviceDatabases.value.values.forEach {
            it.forEach { db ->
                if (db.id == databaseId) {
                    return db
                }
            }
        }
        return null
    }

    override fun observeDeviceDatabases(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceDataBaseDomainModel>> = deviceDatabases.map { it[deviceIdAndPackageName] ?: emptyList() }

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

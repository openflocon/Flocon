package io.github.openflocon.data.local.adb.datasource

import io.github.openflocon.data.core.adb.datasource.local.AdbLocalDataSource
import io.github.openflocon.data.local.adb.dao.AdbDevicesDao
import io.github.openflocon.data.local.adb.mapper.toDomainModel
import io.github.openflocon.data.local.adb.mapper.toEntity
import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalAdbDataSourceRoom(
    private val dao: AdbDevicesDao,
) : AdbLocalDataSource {

    override val devicesWithSerial: Flow<Set<DeviceWithSerialDomainModel>> =
        dao.getAll().map { entityList ->
            entityList.map { toDomainModel(it) }.toSet()
        }

    override suspend fun add(item: DeviceWithSerialDomainModel) {
        dao.insert(toEntity(item))
    }

    override suspend fun getFromDeviceId(deviceId: String): DeviceWithSerialDomainModel? = dao.getFromDeviceId(deviceId)?.let { toDomainModel(it) }
}

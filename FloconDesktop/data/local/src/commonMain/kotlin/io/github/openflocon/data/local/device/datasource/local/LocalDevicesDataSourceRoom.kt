package io.github.openflocon.data.local.device.datasource.local

import io.github.openflocon.data.core.device.datasource.local.LocalDevicesDataSource
import io.github.openflocon.data.core.device.datasource.local.model.InsertResult
import io.github.openflocon.data.local.device.datasource.dao.DevicesDao
import io.github.openflocon.data.local.device.datasource.mapper.toDomainModel
import io.github.openflocon.data.local.device.datasource.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDevicesDataSourceRoom(
    private val dao: DevicesDao
) : LocalDevicesDataSource {

    override val devices: Flow<List<DeviceDomainModel>>
        get() = dao.observeDevices()
            .map { it.map { device -> device.toDomainModel() } }

    override fun observeDeviceById(it: DeviceId): Flow<DeviceDomainModel?> {
        return dao.observeDeviceById(deviceId = it).map { it?.toDomainModel() }
    }

    override suspend fun getDeviceById(it: DeviceId): DeviceDomainModel? {
        return dao.getDeviceById(deviceId = it)?.toDomainModel()
    }

    override suspend fun insertDevice(device: DeviceDomainModel): InsertResult {
        val deviceEntity = dao.getDeviceById(device.deviceId)
        if (deviceEntity != null) {
            return InsertResult.Exists
        } else {
            dao.insertDevice(device.toEntity())
            return InsertResult.New
        }
    }

    override suspend fun insertDeviceApp(
        deviceId: DeviceId,
        app: DeviceAppDomainModel
    ): InsertResult {
        val appEntity =
            dao.getDeviceAppByPackageName(deviceId = deviceId, packageName = app.packageName)
        if (appEntity != null) {
            return InsertResult.Exists
        } else {
            dao.insertDeviceApp(
                app.toEntity(
                    parentDeviceId = deviceId,
                )
            )
            return InsertResult.New
        }
    }

    override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> {
        return dao.observeDeviceApps(deviceId = deviceId)
            .map { it.map { deviceApp -> deviceApp.toDomainModel() } }
    }

    override suspend fun getDeviceAppByPackage(
        deviceId: DeviceId,
        packageName: String
    ): DeviceAppDomainModel? {
        return dao.getDeviceAppByPackageName(deviceId, packageName)?.toDomainModel()
    }

    override suspend fun saveAppIcon(
        deviceId: DeviceId,
        appPackageName: String,
        iconEncoded: String
    ) {
        dao.updateAppIcon(
            deviceId = deviceId,
            packageName = appPackageName,
            iconEncoded = iconEncoded
        )
    }

    override suspend fun hasAppIcon(
        deviceId: DeviceId,
        appPackageName: String
    ): Boolean {
        return dao.hasAppIcon(deviceId, appPackageName)
    }

    override suspend fun clear() {
        dao.clear()
    }
}

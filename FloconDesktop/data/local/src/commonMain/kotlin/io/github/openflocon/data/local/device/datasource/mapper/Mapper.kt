package io.github.openflocon.data.local.device.datasource.mapper

import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceConnectionType
import io.github.openflocon.domain.device.models.DeviceDomainModel

internal fun DeviceAppEntity.toDomainModel(): DeviceAppDomainModel = DeviceAppDomainModel(
    name = this.name,
    packageName = this.packageName,
    iconEncoded = this.iconEncoded,
    lastAppInstance = this.lastAppInstance,
    floconVersionOnDevice = this.floconVersionOnDevice,
    installedPlugins = this.installedPlugins?.toInstalledPluginsSet(),
)

internal fun DeviceDomainModel.toEntity(): DeviceEntity = DeviceEntity(
    deviceId = this.deviceId,
    deviceName = this.deviceName,
    platform = this.platform,
    connectionType = this.connectionType.toConnectionTypeString(),
)

internal fun DeviceEntity.toDomainModel(): DeviceDomainModel = DeviceDomainModel(
    deviceId = this.deviceId,
    deviceName = this.deviceName,
    platform = this.platform,
    connectionType = this.connectionType.toConnectionType(),
)

internal fun DeviceAppDomainModel.toEntity(deviceId: String): DeviceAppEntity = DeviceAppEntity(
    deviceId = deviceId,
    name = this.name,
    packageName = this.packageName,
    iconEncoded = this.iconEncoded,
    lastAppInstance = this.lastAppInstance,
    floconVersionOnDevice = this.floconVersionOnDevice,
    installedPlugins = this.installedPlugins?.toInstalledPluginsString(),
)

internal fun DeviceConnectionType.toConnectionTypeString(): String = when (this) {
    DeviceConnectionType.Default -> "default"
    DeviceConnectionType.Wifi -> "wifi"
}

internal fun String.toConnectionType(): DeviceConnectionType = if (this == "wifi") DeviceConnectionType.Wifi else DeviceConnectionType.Default

internal fun Set<String>.toInstalledPluginsString(): String = joinToString(",")

internal fun String.toInstalledPluginsSet(): Set<String> = split(",").filter { it.isNotEmpty() }.toSet()

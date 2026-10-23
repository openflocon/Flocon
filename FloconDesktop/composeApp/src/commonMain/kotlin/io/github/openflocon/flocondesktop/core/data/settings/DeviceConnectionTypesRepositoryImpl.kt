package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.repository.DeviceConnectionTypesRepository
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DeviceConnectionTypesRepositoryImpl(
    private val localSettingsDataSource: SettingsDataSource,
) : DeviceConnectionTypesRepository {

    override val lastConnectionTypes: Flow<Map<DeviceId, DeviceConnectionTypeDomainModel>> = localSettingsDataSource.deviceConnectionTypes
        .map { stored ->
            stored.mapNotNull { (deviceId, name) ->
                DeviceConnectionTypeDomainModel.entries
                    .firstOrNull { it.name == name }
                    ?.let { deviceId to it }
            }.toMap()
        }

    override suspend fun setConnectionTypes(types: Map<DeviceId, DeviceConnectionTypeDomainModel>) {
        localSettingsDataSource.setDeviceConnectionTypes(types.mapValues { (_, type) -> type.name })
    }
}

package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkSettingsLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkSettingsDao
import io.github.openflocon.data.local.network.models.NetworkSettingsEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NetworkSettingsLocalDataSourceRoom(
    private val networkSettingsDao: NetworkSettingsDao,
    private val json: Json,
) : NetworkSettingsLocalDataSource {

    override suspend fun getNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
    ): NetworkSettingsDomainModel? {
        return networkSettingsDao.get(
            deviceId = deviceAndApp.deviceId,
            packageName = deviceAndApp.packageName
        ).let {
            it?.toDomain(
                json = json,
            )
        }
    }

    override fun observeNetworkSettings(deviceAndApp: DeviceIdAndPackageNameDomainModel): Flow<NetworkSettingsDomainModel?> {
        return networkSettingsDao.observe(
            deviceId = deviceAndApp.deviceId,
            packageName = deviceAndApp.packageName
        ).map {
            it?.toDomain(
                json = json,
            )
        }
    }

    override suspend fun updateNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        newValue: NetworkSettingsDomainModel
    ) {
        networkSettingsDao.insertOrUpdate(
            newValue.toEntity(
                json = json,
                deviceAndApp = deviceAndApp,
            )
        )
    }
}

private fun NetworkSettingsDomainModel.toEntity(
    deviceAndApp: DeviceIdAndPackageNameDomainModel,
    json: Json,
): NetworkSettingsEntity {
    val saved = try {
        json.encodeToString(this.toSaved())
    } catch (t: Throwable) {
        t.printStackTrace()
        ""
    }
    return NetworkSettingsEntity(
        deviceId = deviceAndApp.deviceId,
        packageName = deviceAndApp.packageName,
        valueAsJson = saved,
    )
}

@Serializable
internal data class NetworkSettingsSavedModel(
    val displayOldSessions: Boolean,
    val autoScroll: Boolean,
    val invertList: Boolean,
)

private fun NetworkSettingsDomainModel.toSaved() = NetworkSettingsSavedModel(
    displayOldSessions = displayOldSessions,
    autoScroll = autoScroll,
    invertList = invertList,
)

private fun NetworkSettingsEntity.toDomain(
    json: Json,
): NetworkSettingsDomainModel {
    val saved = try {
        json.decodeFromString<NetworkSettingsSavedModel>(this.valueAsJson)
    } catch (t: Throwable) {
        t.printStackTrace()
        NetworkSettingsSavedModel(
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
    }
    return NetworkSettingsDomainModel(
        displayOldSessions = saved.displayOldSessions,
        autoScroll = saved.autoScroll,
        invertList = saved.invertList,
    )
}

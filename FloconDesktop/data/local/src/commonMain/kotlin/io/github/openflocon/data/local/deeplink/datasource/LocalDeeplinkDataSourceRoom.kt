package io.github.openflocon.data.local.deeplink.datasource

import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.data.local.deeplink.dao.FloconDeeplinkDao
import io.github.openflocon.data.local.deeplink.dao.FloconDeeplinkVariableDao
import io.github.openflocon.data.local.deeplink.mapper.toDomainModel
import io.github.openflocon.data.local.deeplink.mapper.toDomainModels
import io.github.openflocon.data.local.deeplink.mapper.toEntities
import io.github.openflocon.data.local.deeplink.mapper.toEntity
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.serialization.json.Json

internal class LocalDeeplinkDataSourceRoom(
    private val deeplinkDao: FloconDeeplinkDao,
    private val deeplinkVariableDao: FloconDeeplinkVariableDao,
    private val json: Json,
) : DeeplinkLocalDataSource {

    override suspend fun update(
        deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel,
        deeplinks: Deeplinks
    ) {
        deeplinkDao.updateAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
            deeplinks = toEntities(
                deeplinks = deeplinks.deeplinks,
                deviceIdAndPackageName = deviceIdAndPackageNameDomainModel,
                json = json,
            )
        )
        deeplinkVariableDao.updateAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
            variables = toEntities(
                variables = deeplinks.variables,
                deviceIdAndPackageName = deviceIdAndPackageNameDomainModel
            )
        )
    }

    override fun observe(
        deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel
    ): Flow<Deeplinks> = combine(
        deeplinkDao.observeAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
        )
            .map { toDomainModels(entities = it, json = json) }
            .distinctUntilChanged(),
        deeplinkVariableDao.observeAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
        )
            .map { toDomainModels(entities = it) }
            .distinctUntilChanged()
    ) { deeplinks, variables ->
        Deeplinks(
            deeplinks = deeplinks,
            variables = variables
        )
    }
        .onEmpty { emit(Deeplinks(emptyList(), emptyList())) }

    override fun observeHistory(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> =
        deeplinkDao.observeHistory(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
        )
            .map { toDomainModels(it, json = json) }
            .distinctUntilChanged()

    override suspend fun addToHistory(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        item: DeeplinkDomainModel
    ) {
        deeplinkDao.insert(
            deeplink = item.toEntity(
                deviceIdAndPackageName = deviceIdAndPackageName,
                isHistory = true,
                json = json,
            )
        )
    }

    override suspend fun removeFromHistory(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        deeplinkId: Long,
    ) {
        deeplinkDao.delete(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            deeplinkId = deeplinkId,
            isHistory = true,
        )
    }

    override suspend fun getDeeplinkById(
        deeplinkId: Long,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): DeeplinkDomainModel? = deeplinkDao.getById(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        deeplinkId = deeplinkId,
    )?.toDomainModel(
        json = json,
    )
}

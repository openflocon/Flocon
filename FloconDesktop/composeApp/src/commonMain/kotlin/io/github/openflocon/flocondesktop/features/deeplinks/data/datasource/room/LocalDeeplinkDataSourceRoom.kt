package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room

import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.LocalDeeplinkDataSource
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.mapper.toDomainModels
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.mapper.toEntities
import io.github.openflocon.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class LocalDeeplinkDataSourceRoom(
    private val deeplinkDao: FloconDeeplinkDao,
) : LocalDeeplinkDataSource {
    override suspend fun update(deviceId: String, deeplinks: List<DeeplinkDomainModel>) {
        deeplinkDao.updateAll(
            deviceId = deviceId,
            deeplinks = toEntities(
                deeplinks = deeplinks,
                deviceId = deviceId,
            ),
        )
    }

    override fun observe(deviceId: String): Flow<List<DeeplinkDomainModel>> = deeplinkDao.observeAll(deviceId = deviceId)
        .map { toDomainModels(it) }
        .distinctUntilChanged()
}

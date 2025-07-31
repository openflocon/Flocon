package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource

import io.github.openflocon.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalDeeplinkDataSource {
    suspend fun update(deviceId: String, deeplinks: List<DeeplinkDomainModel>)
    fun observe(deviceId: String): Flow<List<DeeplinkDomainModel>>
}

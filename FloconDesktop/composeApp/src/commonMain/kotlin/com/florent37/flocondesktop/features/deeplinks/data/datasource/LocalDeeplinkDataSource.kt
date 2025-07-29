package com.florent37.flocondesktop.features.deeplinks.data.datasource

import com.florent37.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalDeeplinkDataSource {
    suspend fun update(deviceId: String, deeplinks: List<DeeplinkDomainModel>)
    fun observe(deviceId: String): Flow<List<DeeplinkDomainModel>>
}

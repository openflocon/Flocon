package io.github.openflocon.flocondesktop.features.deeplinks.domain.repository

import com.florent37.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {
    fun executeDeeplink(
        adbPath: String,
        deviceId: String,
        deeplink: String,
        packageName: String,
    )
    fun observe(deviceId: String): Flow<List<DeeplinkDomainModel>>
}

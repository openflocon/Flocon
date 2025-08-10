package io.github.openflocon.flocondesktop.features.deeplinks.domain.repository

import io.github.openflocon.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {

    fun executeDeeplink(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        adbPath: String,
        deeplink: String,
    )

    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>

}

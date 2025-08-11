package io.github.openflocon.flocondesktop.features.deeplinks.domain.repository

import io.github.openflocon.domain.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {

    fun executeDeeplink(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        adbPath: String,
        deeplink: String,
    )

    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

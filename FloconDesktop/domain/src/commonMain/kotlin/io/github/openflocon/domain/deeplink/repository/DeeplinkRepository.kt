package io.github.openflocon.domain.deeplink.repository

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

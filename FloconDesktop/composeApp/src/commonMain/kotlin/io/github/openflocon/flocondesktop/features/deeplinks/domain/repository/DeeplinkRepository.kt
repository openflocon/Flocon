package io.github.openflocon.flocondesktop.features.deeplinks.domain.repository

import com.flocon.library.domain.models.DeeplinkDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {

    fun executeDeeplink(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        adbPath: String,
        deeplink: String,
    )

    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

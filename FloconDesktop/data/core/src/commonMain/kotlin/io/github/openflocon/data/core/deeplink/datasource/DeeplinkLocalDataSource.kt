package io.github.openflocon.data.core.deeplink.datasource

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.deeplink.models.Deeplinks
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkLocalDataSource {

    suspend fun update(
        deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel,
        deeplinks: Deeplinks
    )

    fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<Deeplinks>

    fun observeHistory(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>

    suspend fun addToHistory(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        item: DeeplinkDomainModel
    )

    suspend fun removeFromHistory(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        deeplinkId: Long,
    )

    suspend fun getDeeplinkById(
        deeplinkId: Long,
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): DeeplinkDomainModel?

}

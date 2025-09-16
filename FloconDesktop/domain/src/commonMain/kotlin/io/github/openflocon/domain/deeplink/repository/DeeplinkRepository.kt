package io.github.openflocon.domain.deeplink.repository

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkRepository {
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
    suspend fun getDeeplinkById(deeplinkId: Long, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): DeeplinkDomainModel?
    fun observeHistory(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
    suspend fun addToHistory(item: DeeplinkDomainModel, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
    suspend fun removeFromHistory(deeplinkId: Long, deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

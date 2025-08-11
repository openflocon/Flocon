package io.github.openflocon.data.core.deeplink.datasource

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface DeeplinkLocalDataSource {

    suspend fun update(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>)

    fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

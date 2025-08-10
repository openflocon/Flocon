package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource

import io.github.openflocon.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalDeeplinkDataSource {

    suspend fun update(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>)

    fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

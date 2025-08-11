package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource

import com.flocon.library.domain.models.DeeplinkDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface LocalDeeplinkDataSource {

    suspend fun update(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>)

    fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>>
}

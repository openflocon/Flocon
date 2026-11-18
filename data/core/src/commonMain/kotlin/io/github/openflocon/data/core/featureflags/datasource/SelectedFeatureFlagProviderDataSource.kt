package io.github.openflocon.data.core.featureflags.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface SelectedFeatureFlagProviderDataSource {

    fun observeSelectedProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<String?>

    fun selectProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
    )
}

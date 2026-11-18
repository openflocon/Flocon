package io.github.openflocon.data.local.featureflags.datasource

import io.github.openflocon.data.core.featureflags.datasource.SelectedFeatureFlagProviderDataSource
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SelectedFeatureFlagProviderDataSourceInMemory : SelectedFeatureFlagProviderDataSource {
    private val selectedProviders = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, String>>(emptyMap())

    override fun observeSelectedProvider(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<String?> = selectedProviders
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun selectProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
    ) {
        selectedProviders.update {
            it + (deviceIdAndPackageName to provider)
        }
    }
}

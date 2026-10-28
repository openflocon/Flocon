package io.github.openflocon.data.local.accessibility.datasource

import io.github.openflocon.data.core.accessibility.datasource.AccessibilityStateDataSource
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AccessibilityStateDataSourceInMemory : AccessibilityStateDataSource {
    private val states = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, AccessibilityStateDomainModel>>(emptyMap())

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AccessibilityStateDomainModel?> = states
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun get(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): AccessibilityStateDomainModel? = states.value[deviceIdAndPackageName]

    override fun set(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        state: AccessibilityStateDomainModel,
    ) {
        states.update { it + (deviceIdAndPackageName to state) }
    }
}

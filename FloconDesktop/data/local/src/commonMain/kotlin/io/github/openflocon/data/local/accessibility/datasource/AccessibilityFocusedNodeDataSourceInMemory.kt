package io.github.openflocon.data.local.accessibility.datasource

import io.github.openflocon.data.core.accessibility.datasource.AccessibilityFocusedNodeDataSource
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AccessibilityFocusedNodeDataSourceInMemory : AccessibilityFocusedNodeDataSource {
    private val focusedNodes = MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, AccessibilityFocusedNodeDomainModel>>(emptyMap())

    override fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AccessibilityFocusedNodeDomainModel?> = focusedNodes
        .map { it[deviceIdAndPackageName] }
        .distinctUntilChanged()

    override fun set(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        focusedNode: AccessibilityFocusedNodeDomainModel,
    ) {
        focusedNodes.update { it + (deviceIdAndPackageName to focusedNode) }
    }
}

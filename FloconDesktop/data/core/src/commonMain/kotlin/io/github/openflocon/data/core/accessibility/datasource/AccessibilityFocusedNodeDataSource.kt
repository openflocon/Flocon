package io.github.openflocon.data.core.accessibility.datasource

import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * The latest focused node sent by each device, it is not stored: it only makes sense while the app runs
 */
interface AccessibilityFocusedNodeDataSource {
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AccessibilityFocusedNodeDomainModel?>

    fun set(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        focusedNode: AccessibilityFocusedNodeDomainModel,
    )
}

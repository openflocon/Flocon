package io.github.openflocon.data.core.accessibility.datasource

import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * The latest state sent by each device, it is not stored: the device sends it again when it connects
 */
interface AccessibilityStateDataSource {
    fun observe(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<AccessibilityStateDomainModel?>

    fun get(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): AccessibilityStateDomainModel?

    fun set(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        state: AccessibilityStateDomainModel,
    )
}

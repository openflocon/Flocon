package io.github.openflocon.data.core.accessibility.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

interface ToDeviceAccessibilityDataSource {
    suspend fun requestFocusedNode(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun focusNext(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun focusPrevious(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun activate(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}

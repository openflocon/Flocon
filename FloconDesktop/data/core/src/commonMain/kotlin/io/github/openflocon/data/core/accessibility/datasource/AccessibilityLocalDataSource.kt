package io.github.openflocon.data.core.accessibility.datasource

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AccessibilityLocalDataSource {

    suspend fun insert(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        events: List<AccessibilityEventDomainModel>,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
    ): Flow<List<AccessibilityEventDomainModel>>

    fun observeById(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): Flow<AccessibilityEventDomainModel?>

    fun observeTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>>

    suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventDomainModel>

    suspend fun getTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String>

    suspend fun clear(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Int
}

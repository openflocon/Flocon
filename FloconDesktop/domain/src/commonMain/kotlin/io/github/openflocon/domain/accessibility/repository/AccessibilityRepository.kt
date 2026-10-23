package io.github.openflocon.domain.accessibility.repository

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface AccessibilityRepository {

    /**
     * The newest events first, filtered by type and by a text found in the event
     */
    fun observeEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
    ): Flow<List<AccessibilityEventDomainModel>>

    fun observeEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): Flow<AccessibilityEventDomainModel?>

    fun observeTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>>

    /**
     * The latest state sent by the device, null until it sends one
     */
    fun observeState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<AccessibilityStateDomainModel?>

    suspend fun searchEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventDomainModel>

    suspend fun getTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String>

    suspend fun getState(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): AccessibilityStateDomainModel?

    /**
     * @return the number of events removed
     */
    suspend fun clearEvents(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Int
}

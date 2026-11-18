package io.github.openflocon.domain.accessibility.repository

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityFocusedNodeDomainModel
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

    /**
     * The element the screen reader is on, null until the device sends it
     */
    fun observeFocusedNode(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<AccessibilityFocusedNodeDomainModel?>

    /**
     * Asks the device where the screen reader is, the answer comes through [observeFocusedNode]
     */
    suspend fun requestFocusedNode(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    /**
     * Moves the accessibility focus of the device, the answer comes through [observeFocusedNode]
     */
    suspend fun focusNext(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun focusPrevious(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    /**
     * Asks the device to turn TalkBack on or off, the answer comes through [observeState].
     * The device needs the WRITE_SECURE_SETTINGS permission, see [AccessibilityStateDomainModel.canToggleTalkBack]
     */
    suspend fun setTalkBackEnabled(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, enabled: Boolean)

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

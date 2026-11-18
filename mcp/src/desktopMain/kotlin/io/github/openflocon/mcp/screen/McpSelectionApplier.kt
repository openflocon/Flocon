package io.github.openflocon.mcp.screen

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.mcp.McpTargetResolver

/**
 * Changes the device and the app selected in Flocon for a tool that shows something to the user.
 */
internal class McpSelectionApplier(
    private val targetResolver: McpTargetResolver,
    private val selectDeviceUseCase: SelectDeviceUseCase,
    private val selectDeviceAppUseCase: SelectDeviceAppUseCase,
) {
    /** The device and app selected in Flocon, null when there is none */
    suspend fun current(): DeviceIdAndPackageNameDomainModel? = runCatching { targetResolver.resolve(null, null) }.getOrNull()

    /**
     * Selects [target]: its device when it is not the one selected, then its app when [selectApp].
     * @return what was changed, one sentence each, empty when the selection was already this one
     */
    suspend fun select(
        target: DeviceIdAndPackageNameDomainModel,
        current: DeviceIdAndPackageNameDomainModel?,
        selectApp: Boolean,
    ): List<String> = buildList {
        if (current?.deviceId != target.deviceId) {
            selectDeviceUseCase(target.deviceId)
            add("Selected the device ${target.deviceId}")
        }
        if (selectApp) {
            selectDeviceAppUseCase(target.packageName)
            add("Selected the app ${target.packageName}")
        }
    }
}

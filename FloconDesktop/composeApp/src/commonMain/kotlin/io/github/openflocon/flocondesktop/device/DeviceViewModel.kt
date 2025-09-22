package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.device.models.ContentUiState
import io.github.openflocon.flocondesktop.device.models.CpuItem
import io.github.openflocon.flocondesktop.device.models.CpuUiState
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
import io.github.openflocon.flocondesktop.device.models.InfoUiState
import io.github.openflocon.flocondesktop.device.models.MemoryUiState
import io.github.openflocon.flocondesktop.device.models.PermissionItem
import io.github.openflocon.flocondesktop.device.models.PermissionUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DeviceViewModel(
    val deviceId: String,
    val sendCommandUseCase: SendCommandUseCase,
    val deviceSerialUseCase: GetDeviceSerialUseCase,
    val currentDeviceAppsUseCase: GetCurrentDeviceIdAndPackageNameUseCase
) : ViewModel() {

    private val contentState = MutableStateFlow(ContentUiState(selectedIndex = 0))
    private val infoState = MutableStateFlow(
        InfoUiState(
            model = "",
            brand = "",
            versionRelease = "",
            versionSdk = "",
            serialNumber = "",
            battery = ""
        )
    )
    private val memoryState = MutableStateFlow(MemoryUiState(emptyList()))
    private val cpuState = MutableStateFlow(CpuUiState(emptyList()))
    private val permissionState = MutableStateFlow(PermissionUiState(emptyList()))

    val uiState = combine(
        contentState,
        infoState,
        memoryState,
        cpuState,
        permissionState
    ) { content, info, memory, cpu, permission ->
        DeviceUiState(
            contentState = content,
            infoState = info,
            memoryState = memory,
            cpuState = cpu,
            permissionState = permission
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeviceUiState(
                contentState = contentState.value,
                infoState = infoState.value,
                memoryState = memoryState.value,
                cpuState = cpuState.value,
                permissionState = permissionState.value
            )
        )

    private var deviceSerial: String = ""

    init {
        viewModelScope.launch(Dispatchers.IO) {
            deviceSerial = deviceSerialUseCase(deviceId)

            onRefresh()
        }
    }

    fun onAction(action: DeviceAction) {
        when (action) {
            is DeviceAction.SelectTab -> onSelect(action)
            DeviceAction.Refresh -> onRefresh()
            is DeviceAction.ChangePermission -> onChangePermission(action)
        }
    }

    private fun onChangePermission(action: DeviceAction.ChangePermission) {
        viewModelScope.launch {
            if (action.granted) {
                revokePermission(action.permission)
            } else {
                grantPermission(action.permission)
            }
            fetchPermission()
        }
    }

    private fun onSelect(action: DeviceAction.SelectTab) {
        contentState.update { it.copy(selectedIndex = action.index) }
    }

    private fun onRefresh() {
        refreshCpu()
        deviceInfo()
        fetchPermission()
        viewModelScope.launch {
        //                    battery = sendCommand("shell", "dumpsys", "battery"),
//                    mem = sendCommand("shell", "dumpsys", "meminfo")
        }
    }

    private fun deviceInfo() {
        viewModelScope.launch {
            infoState.update { state ->
                state.copy(
                    model = sendCommand("shell", "getprop", "ro.product.model"),
                    brand = sendCommand("shell", "getprop", "ro.product.brand"),
                    versionRelease = sendCommand("shell", "getprop", "ro.build.version.release"),
                    versionSdk = sendCommand("shell", "getprop", "ro.build.version.sdk"),
                    serialNumber = sendCommand("shell", "getprop", "ro.serialno")
                )
            }
        }
    }

    private fun fetchPermission() {
        viewModelScope.launch {
            val packageName = currentDeviceAppsUseCase()?.packageName ?: return@launch
            val command = sendCommand("shell", "dumpsys", "package", packageName)
            val permissions = command.lines()
                .dropWhile { !it.contains("runtime permissions:") }
                .drop(1)
                .takeWhile { it.contains("granted=") }
                .map { it.trim() }
                .filter { it.startsWith(PERMISSION_PREFIX) }
                .mapNotNull { line ->
                    val list = line.split(":")

                    PermissionItem(
                        name = list.getOrNull(0)?.removePrefix(PERMISSION_PREFIX) ?: return@mapNotNull null,
                        granted = list.getOrNull(1)?.contains("granted=true") ?: return@mapNotNull null,
                    )
                }
                .sortedBy(PermissionItem::name)

            permissionState.update { it.copy(list = permissions) }
        }
    }

    private suspend fun grantPermission(permission: String) {
        val packageName = currentDeviceAppsUseCase() ?: return

        sendCommand("shell", "pm", "grant", packageName.packageName, "${PERMISSION_PREFIX}$permission")
    }

    private suspend fun revokePermission(permission: String) {
        val packageName = currentDeviceAppsUseCase() ?: return

        sendCommand("shell", "pm", "revoke", packageName.packageName, "$PERMISSION_PREFIX$permission")
    }

    private suspend fun sendCommand(vararg args: String): String {
        return sendCommandUseCase(deviceSerial, *args)
            .getOrNull()
            .orEmpty()
            .removeSuffix("\n")
    }

    private fun refreshCpu() {
        viewModelScope.launch(Dispatchers.IO) {
            val output = sendCommand("shell", "dumpsys", "cpuinfo")
            val regex = CPU_REGEX.toRegex()
            val items = output.lineSequence()
                .mapNotNull { regex.find(it) }
                .mapNotNull {
                    try {
                        val packageName = it.groupValues[2].split("/")

                        CpuItem(
                            cpuUsage = it.groupValues[1].toDoubleOrNull() ?: return@mapNotNull null,
                            packageName = packageName[1],
                            pId = packageName[0].toIntOrNull() ?: return@mapNotNull null,
                            userPercentage = it.groupValues[3].toDoubleOrNull() ?: return@mapNotNull null,
                            kernelPercentage = it.groupValues[4].toDoubleOrNull() ?: return@mapNotNull null,
                            minorFaults = it.groupValues[5].toIntOrNull(),
                            majorFaults = it.groupValues[6].toIntOrNull()
                        )
                    } catch (e: NumberFormatException) {
                        // Handle parsing errors gracefully (e.g., log the error)
                        null
                    }
                }
                .sortedByDescending(CpuItem::cpuUsage)
                .distinctBy(CpuItem::packageName)
                .toList()

            cpuState.update { it.copy(list = items) }
        }
    }

    companion object {
        private const val PERMISSION_PREFIX = "android.permission."
        private const val CPU_REGEX = """(\d+(?:\.\d+)?)%\s+([^:]+):\s+(\d+(?:\.\d+)?)%\s+user\s+\+\s+(\d+(?:\.\d+)?)%\s+kernel\s+/ faults:\s+(\d+)\s+minor\s+(\d+)\s+major"""
    }

}

package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.device.models.ContentUiState
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
            deviceInfo()
            fetchPermission()
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
        }
    }

    private fun onSelect(action: DeviceAction.SelectTab) {
        contentState.update { it.copy(selectedIndex = action.index) }
    }

    private fun onRefresh() {
        viewModelScope.launch {
//            _uiState.update { state ->
//                state.copy(
//                    cpu = main(sendCommand("shell", "dumpsys", "cpuinfo")),
//                    battery = sendCommand("shell", "dumpsys", "battery"),
//                    mem = sendCommand("shell", "dumpsys", "meminfo")
//                )
//            }
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
                .dropWhile { !it.contains("install permissions:") }
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

    data class ProcessCpuInfo(
        val pid: Int,
        val cpuPercent: Double,
        val name: String
    )

    data class GlobalCpuLoad(
        val load1m: Double,
        val load5m: Double,
        val load15m: Double
    )

    fun parseCpuInfoOutput(output: String): Pair<GlobalCpuLoad?, List<ProcessCpuInfo>> {
        val processes = mutableListOf<ProcessCpuInfo>()
        var globalLoad: GlobalCpuLoad? = null

        // Regex pour capturer les lignes de processus.
        // Similaire à la version Python, peut nécessiter des ajustements.
        // Groupes : 1: CPU%, 2: PID, 3: Name
        val processLineRegex = Regex(
            """^\s*([\d.]+)[k%]?\s+""" + // CPU % (ex: "10.0", "0.0k", "5%")
                """(?:\S+\s+)*?""" +       // Skip other columns non-greedily
                """(\d+)\s+""" +           // PID
                """(?:[a-zA-Z0-9_/.-]+\s+)*?""" + // More potential intermediate columns
                """([a-zA-Z0-9_.-]+(?:[:][a-zA-Z0-9_.-]+)?(?:\s*\[.*?])?)$""" // Process name/package
        )

        val loadAverageRegex = Regex("""Load: (\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)""")

        output.lines().forEach { line ->
            // Recherche de la ligne de charge globale
            loadAverageRegex.find(line)?.let { matchResult ->
                try {
                    globalLoad = GlobalCpuLoad(
                        load1m = matchResult.groupValues[1].toDouble(),
                        load5m = matchResult.groupValues[2].toDouble(),
                        load15m = matchResult.groupValues[3].toDouble()
                    )
                    return@forEach // Similaire à 'continue' dans une boucle classique
                } catch (e: NumberFormatException) {
                    // Gérer le cas où la conversion en Double échoue
                }
            }


            // Recherche des lignes de processus
            processLineRegex.find(line)?.let { matchResult ->
                try {
                    val cpuPercentStr = matchResult.groupValues[1].replace("k", "")
                    val cpuPercent = cpuPercentStr.toDouble()
                    val pid = matchResult.groupValues[2].toInt()
                    val name = matchResult.groupValues[3].trim()

                    // Ignorer les lignes qui ne sont pas de vrais processus
                    if (name.equals("Name", ignoreCase = true) || name.equals("PID", ignoreCase = true)) {
                        return@forEach
                    }

                    processes.add(ProcessCpuInfo(pid, cpuPercent, name))
                } catch (e: NumberFormatException) {
                    // println("Skipping line due to NumberFormatException: $line -> ${e.message}")
                } catch (e: IndexOutOfBoundsException) {
                    // println("Skipping line due to IndexOutOfBoundsException (regex group not found): $line")
                }
            }
        }

        return globalLoad to processes.sortedByDescending { it.cpuPercent }
    }

    fun main(cpuOutput: String?): String {
        println("Récupération des informations CPU...\n")

        if (cpuOutput != null) {
            val (globalLoad, processList) = parseCpuInfoOutput(cpuOutput)

            return buildString {
                globalLoad?.let {
                    appendLine("Charge CPU globale (Load Average):")
                    appendLine("  1 min: %.2f".format(it.load1m))
                    appendLine("  5 min: %.2f".format(it.load5m))
                    appendLine(" 15 min: %.2f".format(it.load15m))
                    appendLine("-".repeat(30))
                }

                if (processList.isNotEmpty()) {
                    appendLine("%-8s %-7s %s".format("CPU%", "PID", "Nom du Processus"))
                    appendLine("%-8s %-7s %s".format("----", "---", "----------------"))
                    processList.forEach { proc ->
                        appendLine("%-8.1f %-7d %s".format(proc.cpuPercent, proc.pid, proc.name))
                    }
                } else {
                    appendLine("Aucun processus n'a pu être parsé.")
                    appendLine("\nSortie brute pour débogage (premiers 1000 caractères):")
                    appendLine(cpuOutput.take(1000))
                }
            }
        } else {
            println("Impossible de récupérer la sortie de dumpsys cpuinfo.")
        }

        return ""
    }

    companion object {
        private const val PERMISSION_PREFIX = "android.permission."
    }

}

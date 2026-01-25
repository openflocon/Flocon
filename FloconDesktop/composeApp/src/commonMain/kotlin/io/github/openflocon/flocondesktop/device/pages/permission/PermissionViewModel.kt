package io.github.openflocon.flocondesktop.device.pages.permission

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionViewModel(
    deviceSerial: String,
    sendCommandUseCase: SendCommandUseCase,
    val currentDeviceAppsUseCase: GetCurrentDeviceIdAndPackageNameUseCase
) : PageViewModel(deviceSerial = deviceSerial, sendCommandUseCase = sendCommandUseCase) {

    private val _uiState = MutableStateFlow(PermissionUiState(emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPermission()
    }

    fun onAction(action: PermissionAction) {
        when (action) {
            is PermissionAction.ChangePermission -> onChangePermission(action)
        }
    }

    private fun onChangePermission(action: PermissionAction.ChangePermission) {
        viewModelScope.launch(Dispatchers.IO) {
            if (action.granted) {
                revokePermission(action.permission)
            } else {
                grantPermission(action.permission)
            }
            fetchPermission()
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

            _uiState.update { it.copy(list = permissions) }
        }
    }

    companion object {
        private const val PERMISSION_PREFIX = "android.permission."
    }

}

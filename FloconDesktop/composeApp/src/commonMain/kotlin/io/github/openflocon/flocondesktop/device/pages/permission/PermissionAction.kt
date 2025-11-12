package io.github.openflocon.flocondesktop.device.pages.permission

sealed interface PermissionAction {

    data class ChangePermission(val permission: String, val granted: Boolean) : PermissionAction


}

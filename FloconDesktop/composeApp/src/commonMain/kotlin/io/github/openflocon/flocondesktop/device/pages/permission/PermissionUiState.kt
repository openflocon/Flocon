package io.github.openflocon.flocondesktop.device.pages.permission

import androidx.compose.runtime.Immutable

@Immutable
data class PermissionUiState(
    val list: List<PermissionItem>
)

@Immutable
data class PermissionItem(
    val name: String,
    val granted: Boolean
)

fun previewPermissionUiState() = PermissionUiState(
    emptyList()
)

package io.github.openflocon.flocondesktop.device.models

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
    list = emptyList()
)

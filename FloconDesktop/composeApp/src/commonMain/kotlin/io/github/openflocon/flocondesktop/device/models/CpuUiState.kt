package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class CpuUiState(
    val list: List<CpuItem>
)

@Immutable
data class CpuItem(
    val name: String
)

fun previewCpuUiState() = CpuUiState(
    list = emptyList()
)

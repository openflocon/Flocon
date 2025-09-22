package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class MemoryUiState(
    val list: List<MemoryItem>
)

@Immutable
data class MemoryItem(
    val name: String
)

fun previewMemoryUiState() = MemoryUiState(
    list = emptyList()
)

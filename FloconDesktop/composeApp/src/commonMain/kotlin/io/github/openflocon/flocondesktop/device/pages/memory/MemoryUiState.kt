package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.compose.runtime.Immutable

@Immutable
data class MemoryUiState(
    val list: List<MemoryItem>
)

@Immutable
data class MemoryItem(
    val memoryUsage: String,
    val processName: String,
    val pid: Int
)

fun previewMemoryUiState() = MemoryUiState(
    list = emptyList()
)

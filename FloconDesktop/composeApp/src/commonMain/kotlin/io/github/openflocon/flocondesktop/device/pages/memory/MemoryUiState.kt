package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MemoryUiState(
    val list: ImmutableList<MemoryItem>
)

@Immutable
data class MemoryItem(
    val memoryUsage: String,
    val processName: String,
    val pid: Int
)

fun previewMemoryUiState() = MemoryUiState(
    list = persistentListOf(
        MemoryItem(memoryUsage = "312.40 MB", processName = "com.example.app", pid = 4212),
        MemoryItem(memoryUsage = "186.07 MB", processName = "system", pid = 1456),
        MemoryItem(memoryUsage = "64.92 MB", processName = "com.google.android.gms", pid = 2310),
    )
)

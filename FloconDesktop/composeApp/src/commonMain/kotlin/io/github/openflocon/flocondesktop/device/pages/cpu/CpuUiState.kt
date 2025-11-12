package io.github.openflocon.flocondesktop.device.pages.cpu

import androidx.compose.runtime.Immutable

@Immutable
data class CpuUiState(
    val list: List<CpuItem>
)

@Immutable
data class CpuItem(
    val cpuUsage: Double,
    val pId: Int,
    val packageName: String,
    val userPercentage: Double,
    val kernelPercentage: Double,
    val minorFaults: Int?,
    val majorFaults: Int?
)

fun previewCpuUiState() = CpuUiState(
    list = emptyList()
)

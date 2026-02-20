package io.github.openflocon.flocondesktop.features.performance.model

import androidx.compose.runtime.Immutable

@Immutable
data class MetricsAverageUiModel(
    val fps: String?,
    val ram: String?,
    val jank: String?,
)
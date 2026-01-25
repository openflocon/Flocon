package io.github.openflocon.flocondesktop.features.performance.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList

@Immutable
data class MetricsGraphsDataUiModel(
    val ram: PersistentList<Double>,
    val fps: PersistentList<Double>,
)
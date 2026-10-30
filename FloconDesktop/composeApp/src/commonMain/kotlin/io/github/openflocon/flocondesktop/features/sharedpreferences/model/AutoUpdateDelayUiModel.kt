package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.sharedpreferences.delayLabel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** A refresh interval proposed by the "Live" control. */
@Immutable
data class AutoUpdateDelayUiModel(
    val delayMs: Int,
    val label: String,
)

private fun autoUpdateDelay(delayMs: Int) = AutoUpdateDelayUiModel(delayMs = delayMs, label = delayLabel(delayMs))

fun autoUpdateDelayPresets(): ImmutableList<AutoUpdateDelayUiModel> = persistentListOf(
    autoUpdateDelay(1_000),
    autoUpdateDelay(2_000),
    autoUpdateDelay(5_000),
    autoUpdateDelay(10_000),
)

package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi

@Immutable
data class FilterUiState(
    val query: String,
    @Deprecated("to remove")
    val methods: List<NetworkMethodUi>
)

fun previewFilterUiState() = FilterUiState(
    query = "",
    methods = emptyList()
)

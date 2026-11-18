package io.github.openflocon.flocondesktop.app.ui.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class AppUiState(
    val test: String
)

private fun previewAppUiState() = AppUiState(
    test = ""
)

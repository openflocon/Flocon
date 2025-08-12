package io.github.openflocon.flocondesktop.features.network.ui.model.mocks

import androidx.compose.runtime.Immutable

@Immutable
data class MockNetworkLineUiModel(
    val id: String,
    val urlPattern: String,
    val method: String,
)

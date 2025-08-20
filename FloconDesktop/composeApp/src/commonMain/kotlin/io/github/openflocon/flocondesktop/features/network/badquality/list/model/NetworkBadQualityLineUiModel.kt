package io.github.openflocon.flocondesktop.features.network.badquality.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkBadQualityLineUiModel(
    val id: String,
    val isEnabled: Boolean,
    val name: String,
)

fun previewNetworkBadQualityLineUiModel() = NetworkBadQualityLineUiModel(
    id = "1",
    isEnabled = true,
    name = "the name",
)

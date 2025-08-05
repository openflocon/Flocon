package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkDetailHeaderUi(
    val name: String,
    val value: String,
)

fun previewNetworkDetailHeaderUi() = NetworkDetailHeaderUi(
    name = "name",
    value = "value",
)

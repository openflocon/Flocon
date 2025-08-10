package io.github.openflocon.flocondesktop.features.network.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkBodyDetailUi(
    val id: String,
    val text: String, // TODO get it from the id & a VM
)

fun previewNetworkBodyDetailUi() = NetworkBodyDetailUi(
    id = "1",
    text = "{  name: \"florent\" }",
)

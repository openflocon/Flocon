package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkBodyDetailUi(
    val text: String, // TODO get it from the id & a VM
)

fun previewNetworkBodyDetailUi() = NetworkBodyDetailUi(
    text = "{  name: \"florent\" }",
)

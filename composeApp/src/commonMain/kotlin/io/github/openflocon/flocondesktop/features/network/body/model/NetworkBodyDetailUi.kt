package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkBodyDetailUi(
    val text: String
)

fun previewNetworkBodyDetailUi() = NetworkBodyDetailUi(
    text = "{  name: \"florent\" }",
)

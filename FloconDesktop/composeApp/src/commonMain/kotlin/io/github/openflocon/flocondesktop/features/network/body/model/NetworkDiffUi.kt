package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable

@Immutable
data class NetworkDiffUi(
    val json: String,
    val clipboardJson: String
)

fun previewNetworkDiffUi() = NetworkDiffUi(
    json = "{ name: \"florent\" }",
    clipboardJson = "{ name: \"raphael\" }"
)

package io.github.openflocon.flocon.plugins.deeplinks.model

import kotlinx.serialization.Serializable

@Serializable
internal class DeeplinkParameterRemote(
    val paramName: String,
    val autoComplete: List<String>,
)

@Serializable
internal class DeeplinkRemote(
    val label: String? = null,
    val link: String,
    val description: String? = null,
    val parameters: List<DeeplinkParameterRemote>,
)

@Serializable
internal class DeeplinksRemote(
    val deeplinks: List<DeeplinkRemote>,
)

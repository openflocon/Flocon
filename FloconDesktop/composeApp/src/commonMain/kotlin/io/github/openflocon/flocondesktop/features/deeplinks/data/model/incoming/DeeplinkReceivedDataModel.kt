package io.github.openflocon.flocondesktop.features.deeplinks.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
data class DeeplinkReceivedDataModel(
    val label: String? = null,
    val link: String,
    val description: String? = null,
)

package com.florent37.flocondesktop.features.deeplinks.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
data class DeeplinksReceivedDataModel(
    val deeplinks: List<DeeplinkReceivedDataModel>,
)

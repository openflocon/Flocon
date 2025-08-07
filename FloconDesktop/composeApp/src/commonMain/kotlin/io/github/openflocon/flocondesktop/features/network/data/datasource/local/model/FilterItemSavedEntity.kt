package io.github.openflocon.flocondesktop.features.network.data.datasource.local.model

import kotlinx.serialization.Serializable

@Serializable
data class FilterItemSavedEntity(
    val text: String,
    val isActive: Boolean,
    val isExcluded: Boolean,
)

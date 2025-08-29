package io.github.openflocon.data.local.network.models

import kotlinx.serialization.Serializable

@Serializable
data class FilterItemSavedEntity(
    val text: String,
    val isActive: Boolean,
    val isExcluded: Boolean,
    val isRegex: Boolean,
)

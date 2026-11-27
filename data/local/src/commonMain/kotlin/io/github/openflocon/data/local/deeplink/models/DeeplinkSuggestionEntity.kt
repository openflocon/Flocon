package io.github.openflocon.data.local.deeplink.models

import kotlinx.serialization.Serializable

/** Stored inside the JSON of a deeplink parameter or of a variable mode, not a Room table. */
@Serializable
data class DeeplinkSuggestionEntity(
    val value: String,
    val label: String? = null,
    val isDefault: Boolean = false,
)

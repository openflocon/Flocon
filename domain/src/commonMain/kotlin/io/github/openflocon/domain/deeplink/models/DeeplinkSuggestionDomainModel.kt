package io.github.openflocon.domain.deeplink.models

/**
 * A suggested value of a deeplink parameter or variable.
 *
 * @property value what is inserted in the field
 * @property label what is displayed instead of the value, when the app gave one
 * @property isDefault pre-selected when the field is empty
 */
data class DeeplinkSuggestionDomainModel(
    val value: String,
    val label: String? = null,
    val isDefault: Boolean = false,
)

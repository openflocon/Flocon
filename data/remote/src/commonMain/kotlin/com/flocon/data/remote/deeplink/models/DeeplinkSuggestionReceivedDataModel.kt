package com.flocon.data.remote.deeplink.models

import io.github.openflocon.domain.deeplink.models.DeeplinkSuggestionDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class DeeplinkSuggestionReceivedDataModel(
    val value: String,
    val label: String? = null,
    val isDefault: Boolean = false,
)

/** The labelled suggestions when the app sent some, else the plain values. Only the first default is kept. */
internal fun toDomainSuggestions(
    values: List<String>,
    suggestions: List<DeeplinkSuggestionReceivedDataModel>?,
): List<DeeplinkSuggestionDomainModel> {
    val all = suggestions
        ?.map { DeeplinkSuggestionDomainModel(value = it.value, label = it.label, isDefault = it.isDefault) }
        ?: values.map { DeeplinkSuggestionDomainModel(value = it) }
    val defaultValue = all.firstOrNull { it.isDefault }?.value
    return all.map { it.copy(isDefault = it.value == defaultValue) }
}

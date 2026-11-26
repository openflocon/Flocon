package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

/** A suggested value: [value] is what goes in the link, [label] what is shown instead when the app gave one. */
@Immutable
data class DeeplinkSuggestionUiModel(
    val value: String,
    val label: String? = null,
    val isDefault: Boolean = false,
) {
    val text: String get() = label ?: value
}

fun previewDeeplinkSuggestionUiModel(
    value: String = "4821",
    label: String? = null,
    isDefault: Boolean = false,
) = DeeplinkSuggestionUiModel(value = value, label = label, isDefault = isDefault)

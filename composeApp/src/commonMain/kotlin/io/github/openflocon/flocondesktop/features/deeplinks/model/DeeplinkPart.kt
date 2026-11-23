package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface DeeplinkPart {

    @Immutable
    data class Text(
        val value: String
    ) : DeeplinkPart

    @Immutable
    data class TextField(
        val label: String,
        val suggestions: ImmutableList<DeeplinkSuggestionUiModel>? = null,
    ) : DeeplinkPart {
        /** What the field holds before the user types, when the app marked a suggestion as default. */
        val defaultValue: String? get() = suggestions?.firstOrNull { it.isDefault }?.value
    }

    /**
     * [value] is the resolved value, or "{name}" while the variable has none ([isResolved] is false).
     */
    @Immutable
    data class Variable(
        val value: String,
        val name: String = value,
        val isResolved: Boolean = true,
    ) : DeeplinkPart
}

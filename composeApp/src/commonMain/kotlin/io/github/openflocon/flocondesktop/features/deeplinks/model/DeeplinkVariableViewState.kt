package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class DeeplinkVariableViewState(
    val name: String,
    val description: String?,
    val value: String,
    val mode: Mode = Mode.Input,
) {
    @Immutable
    sealed interface Mode {
        data object Input : Mode

        @Immutable data class AutoComplete(val suggestions: ImmutableList<DeeplinkSuggestionUiModel>) : Mode
    }
}

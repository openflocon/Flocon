package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkVariableViewState(
    val name: String,
    val description: String?,
    val value: String,
    val mode: Mode = Mode.Input,
    // number of declared deeplinks that use this variable
    val usedByCount: Int = 0,
) {
    @Immutable
    sealed interface Mode {
        data object Input : Mode

        @Immutable data class AutoComplete(val suggestions: List<String>) : Mode
    }
}

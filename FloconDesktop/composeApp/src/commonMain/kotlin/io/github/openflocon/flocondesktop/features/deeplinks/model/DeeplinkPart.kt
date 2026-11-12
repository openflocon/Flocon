package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeeplinkPart {

    @Immutable
    data class Text(
        val value: String
    ) : DeeplinkPart

    @Immutable
    data class TextField(
        val label: String,
        val autoComplete: List<String>? = null,
    ) : DeeplinkPart

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

package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeeplinkPart {
    @Immutable
    data class Text(val value: String) : DeeplinkPart

    @Immutable
    data class TextField(
        val label: String,
        val autoComplete: List<String>?,
    ) : DeeplinkPart
}

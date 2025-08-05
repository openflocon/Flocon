package io.github.openflocon.flocondesktop.features.deeplinks.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeeplinkPart {
    @Immutable
    data class Text(val value: String) : DeeplinkPart

    @Immutable
    data class TextField(val label: String) : DeeplinkPart
}

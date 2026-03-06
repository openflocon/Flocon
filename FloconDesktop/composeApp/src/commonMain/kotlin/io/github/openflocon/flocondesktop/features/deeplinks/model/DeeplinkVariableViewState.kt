package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkVariableViewState(
    val name: String,
    val description: String?,
    val value: String,
)

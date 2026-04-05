package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkScreenState(
    val deepLinks: List<DeeplinkViewState>,
    val variables: List<DeeplinkVariableViewState>,
)

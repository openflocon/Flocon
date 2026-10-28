package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkScreenState(
    val deepLinks: List<DeeplinkViewState>,
    val variables: List<DeeplinkVariableViewState>,
    // the app declared no deeplink and no variable
    val showEmptyState: Boolean = false,
    // false when deeplinks can't be opened on the current device (adb is unavailable over wifi)
    val canSend: Boolean = true,
)

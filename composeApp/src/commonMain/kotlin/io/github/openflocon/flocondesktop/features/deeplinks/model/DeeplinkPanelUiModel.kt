package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkPanelUiModel(
    val link: String,
    val label: String?,
    val description: String?,
)

fun previewDeeplinkPanelUiModel() = DeeplinkPanelUiModel(
    link = "myapp://profile/42",
    label = "Profile",
    description = "Opens the profile screen",
)

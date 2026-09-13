package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource

@Immutable
data class MenuSection(
    val title: StringResource,
    val items: List<MenuItem>,
)

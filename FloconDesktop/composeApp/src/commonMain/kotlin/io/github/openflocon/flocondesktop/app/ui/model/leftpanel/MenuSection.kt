package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import org.jetbrains.compose.resources.StringResource

data class MenuSection(
    val title: StringResource,
    val items: List<MenuItem>,
)

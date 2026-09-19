package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource

@Immutable
data class MenuSection(
    val title: StringResource,
    val items: ImmutableList<MenuItem>,
)

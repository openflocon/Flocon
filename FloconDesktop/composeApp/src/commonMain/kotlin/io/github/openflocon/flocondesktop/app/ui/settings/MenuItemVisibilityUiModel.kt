package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.id
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuItem
import io.github.openflocon.flocondesktop.app.ui.view.displayName
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

@Immutable
data class MenuItemVisibilityUiModel(
    val id: String,
    val name: StringResource,
    val icon: ImageVector,
    val isVisible: Boolean,
)

internal fun MenuItem.toVisibilityUiModel(hiddenIds: Set<String>) = MenuItemVisibilityUiModel(
    id = screen.id,
    name = screen.displayName(),
    icon = icon,
    isVisible = screen.id !in hiddenIds,
)

fun previewMenuItemVisibilities() = persistentListOf(
    MenuItemVisibilityUiModel(SubScreen.Network.id, SubScreen.Network.displayName(), Icons.Outlined.Settings, true),
    MenuItemVisibilityUiModel(SubScreen.Files.id, SubScreen.Files.displayName(), Icons.Outlined.Settings, false),
)

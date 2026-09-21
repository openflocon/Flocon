package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_actions
import flocondesktop.composeapp.generated.resources.menu_data
import flocondesktop.composeapp.generated.resources.menu_network
import flocondesktop.composeapp.generated.resources.menu_storage
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.view.displayName
import io.github.openflocon.flocondesktop.app.ui.view.icon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.getString

@Immutable
data class MenuState(
    val sections: ImmutableList<MenuSection>,
    val bottomItems: ImmutableList<MenuItem>,
)

fun previewMenuState() = MenuState(
    bottomItems = persistentListOf(
        MenuItem(
            screen = SubScreen.Settings,
            icon = Icons.Outlined.Settings,
            isEnabled = true,
        ),
    ),
    sections = persistentListOf(
        MenuSection(
            title = Res.string.menu_network,
            items = persistentListOf(
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Images,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
            ),
        ),
        MenuSection(
            title = Res.string.menu_storage,
            items = persistentListOf(
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.SharedPreferences,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Files,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
            ),
        ),
        MenuSection(
            title = Res.string.menu_data,
            items = persistentListOf(
                MenuItem(
                    screen = SubScreen.Dashboard,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Tables,
                    icon = Icons.Outlined.Settings,
                    isEnabled = true,
                ),
            ),
        ),
    ),
)

internal fun buildMenu() = MenuState(
    bottomItems = persistentListOf(
        item(subScreen = SubScreen.Settings)
    ),
    sections = persistentListOf(
        MenuSection(
            title = Res.string.menu_network,
            items = persistentListOf(
                item(subScreen = SubScreen.Network),
                item(subScreen = SubScreen.Images),
            ),
        ),
        MenuSection(
            title = Res.string.menu_storage,
            items = persistentListOf(
                item(SubScreen.Database),
                item(SubScreen.SharedPreferences),
                item(SubScreen.Files),
            ),
        ),
        MenuSection(
            title = Res.string.menu_data,
            items = persistentListOf(
                item(SubScreen.Dashboard),
                item(SubScreen.Analytics),
                item(SubScreen.Tables),
                item(SubScreen.CrashReporter),
            ),
        ),
        MenuSection(
            title = Res.string.menu_actions,
            items = persistentListOf(
                item(SubScreen.Deeplinks),
                item(SubScreen.AdbCommander),
            ),
        ),
    ),
)

private fun item(
    subScreen: SubScreen
): MenuItem = MenuItem(
    screen = subScreen,
    icon = subScreen.icon(),
    isEnabled = true
)

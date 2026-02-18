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
import org.jetbrains.compose.resources.getString

@Immutable
data class MenuState(
    val sections: List<MenuSection>,
    val bottomItems: List<MenuItem>,
)

fun previewMenuState() = MenuState(
    bottomItems = listOf(
        MenuItem(
            screen = SubScreen.Settings,
            icon = Icons.Outlined.Settings,
            isEnabled = true,
        ),
    ),
    sections = listOf(
        MenuSection(
            title = Res.string.menu_network,
            items = listOf(
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
            items = listOf(
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
            items = listOf(
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
    bottomItems = listOf(
        item(subScreen = SubScreen.Settings)
    ),
    sections = listOf(
        MenuSection(
            title = Res.string.menu_network,
            items = listOf(
                item(subScreen = SubScreen.Network),
                item(subScreen = SubScreen.Images),
            ),
        ),
        MenuSection(
            title = Res.string.menu_storage,
            items = listOf(
                item(SubScreen.Database),
                item(SubScreen.SharedPreferences),
                item(SubScreen.Files),
            ),
        ),
        MenuSection(
            title = Res.string.menu_data,
            items = listOf(
                item(SubScreen.Dashboard),
                item(SubScreen.Analytics),
                item(SubScreen.Tables),
                item(SubScreen.CrashReporter),
            ),
        ),
        MenuSection(
            title = Res.string.menu_actions,
            items = listOf(
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

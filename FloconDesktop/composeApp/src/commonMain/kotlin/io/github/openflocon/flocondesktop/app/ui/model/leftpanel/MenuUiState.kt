package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.view.displayName
import io.github.openflocon.flocondesktop.app.ui.view.icon

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
            text = "Settings",
            isEnabled = true,
        ),
    ),
    sections = listOf(
        MenuSection(
            title = "Network",
            items = listOf(
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Http",
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Images,
                    icon = Icons.Outlined.Settings,
                    text = "Images",
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Grpc",
                    isEnabled = true,
                ),
            ),
        ),
        MenuSection(
            title = "Storage",
            items = listOf(
                MenuItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Database",
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.SharedPreferences,
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences",
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Files,
                    icon = Icons.Outlined.Settings,
                    text = "Files",
                    isEnabled = true,
                ),
            ),
        ),
        MenuSection(
            title = "Data",
            items = listOf(
                MenuItem(
                    screen = SubScreen.Dashboard,
                    icon = Icons.Outlined.Settings,
                    text = "Dashboard",
                    isEnabled = true,
                ),
                MenuItem(
                    screen = SubScreen.Tables,
                    icon = Icons.Outlined.Settings,
                    text = "Tables",
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
            title = "Network",
            items = listOf(
                item(subScreen = SubScreen.Network),
                item(subScreen = SubScreen.Images),
            ),
        ),
        MenuSection(
            title = "Storage",
            items = listOf(
                item(SubScreen.Database),
                item(SubScreen.SharedPreferences),
                item(SubScreen.Files),
            ),
        ),
        MenuSection(
            title = "Data",
            items = listOf(
                item(SubScreen.Dashboard),
                item(SubScreen.Analytics),
                item(SubScreen.Tables),
                item(SubScreen.CrashReporter),
            ),
        ),
        MenuSection(
            title = "Actions",
            items = listOf(
                item(SubScreen.Deeplinks)
            ),
        ),
    ),
)

private fun item(
    subScreen: SubScreen
): MenuItem = MenuItem(
    screen = subScreen,
    icon = subScreen.icon(),
    text = subScreen.displayName(),
    isEnabled = true
)

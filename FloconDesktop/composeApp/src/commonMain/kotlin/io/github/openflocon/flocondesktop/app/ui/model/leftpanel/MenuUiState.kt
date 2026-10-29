package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_actions
import flocondesktop.composeapp.generated.resources.menu_data
import flocondesktop.composeapp.generated.resources.menu_network
import flocondesktop.composeapp.generated.resources.menu_storage
import io.github.openflocon.domain.device.models.DevicePlugin
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.id
import io.github.openflocon.flocondesktop.app.ui.view.displayName
import io.github.openflocon.flocondesktop.app.ui.view.icon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.getString

@Immutable
data class MenuState(
    // always at the top, outside of any section, never pinned
    val topItems: ImmutableList<MenuItem> = persistentListOf(),
    val pinnedItems: ImmutableList<MenuItem> = persistentListOf(),
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
            id = "network",
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
            id = "storage",
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
            id = "data",
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
    topItems = persistentListOf(
        // merges the events of every plugin, not related to a single plugin: always displayed
        item(subScreen = SubScreen.Timeline, pinnable = false),
    ),
    bottomItems = persistentListOf(
        item(subScreen = SubScreen.Settings, pinnable = false)
    ),
    sections = persistentListOf(
        MenuSection(
            id = "network",
            title = Res.string.menu_network,
            items = persistentListOf(
                item(subScreen = SubScreen.Network, plugin = DevicePlugin.Network),
                item(subScreen = SubScreen.Images, plugin = DevicePlugin.Images),
            ),
        ),
        MenuSection(
            id = "storage",
            title = Res.string.menu_storage,
            items = persistentListOf(
                item(SubScreen.Database, plugin = DevicePlugin.Database),
                item(SubScreen.SharedPreferences, plugin = DevicePlugin.SharedPreferences),
                item(SubScreen.Files, plugin = DevicePlugin.Files),
            ),
        ),
        MenuSection(
            id = "data",
            title = Res.string.menu_data,
            items = persistentListOf(
                item(SubScreen.Dashboard, plugin = DevicePlugin.Dashboard),
                item(SubScreen.Analytics, plugin = DevicePlugin.Analytics),
                item(SubScreen.Tables, plugin = DevicePlugin.Tables),
                item(SubScreen.CrashReporter, plugin = DevicePlugin.CrashReporter),
                item(SubScreen.FeatureFlags, plugin = DevicePlugin.FeatureFlags),
                item(SubScreen.Accessibility, plugin = DevicePlugin.Accessibility),
            ),
        ),
        MenuSection(
            id = "actions",
            title = Res.string.menu_actions,
            items = persistentListOf(
                item(SubScreen.Deeplinks, plugin = DevicePlugin.Deeplinks),
                // adb only, not related to a plugin of the SDK: always displayed
                item(SubScreen.AdbCommander),
            ),
        ),
    ),
)

private fun item(
    subScreen: SubScreen,
    plugin: DevicePlugin? = null,
    pinnable: Boolean = true,
): MenuItem = MenuItem(
    screen = subScreen,
    icon = subScreen.icon(),
    isEnabled = true,
    requiredPlugin = plugin,
    isPinnable = pinnable,
)

// the items the user can choose to hide in the settings
internal fun hideableMenuItems(): List<MenuItem> = buildMenu().sections
    .flatMap { it.items }
    .filter { it.isPinnable }

// the screens the user can choose to start the app on: the timeline, and the plugins that are not hidden
internal fun startScreenCandidates(hiddenIds: Set<String>): List<MenuItem> = buildMenu().topItems +
    hideableMenuItems().filterNot { it.screen.id in hiddenIds }

/**
 * - hides the items whose plugin is not installed in the SDK of the app ([installedPlugins] null = unknown, nothing is hidden)
 * - hides the items the user turned off in the settings ([hiddenIds])
 * - moves the pinned items to the top, in the order they have been pinned
 * - marks the collapsed sections
 */
internal fun MenuState.applyPreferences(
    installedPlugins: Set<String>?,
    pinnedIds: List<String>,
    collapsedSectionIds: Set<String>,
    hiddenIds: Set<String> = emptySet(),
): MenuState {
    val visibleSections = sections.map { section ->
        section.copy(
            items = section.items
                .filter { it.requiredPlugin?.isInstalled(installedPlugins) ?: true }
                .filterNot { it.isPinnable && it.screen.id in hiddenIds }
                .toImmutableList(),
        )
    }
    val visibleItems = visibleSections.flatMap { it.items }
    val pinned = pinnedIds.mapNotNull { id ->
        visibleItems.firstOrNull { it.screen.id == id && it.isPinnable }
    }
    val pinnedScreens = pinned.map { it.screen }.toSet()

    return copy(
        pinnedItems = pinned.map { it.copy(isPinned = true) }.toImmutableList(),
        sections = visibleSections
            .map { section ->
                section.copy(
                    items = section.items.filterNot { it.screen in pinnedScreens }.toImmutableList(),
                    isCollapsed = section.id in collapsedSectionIds,
                )
            }
            .filter { it.items.isNotEmpty() }
            .toImmutableList(),
    )
}

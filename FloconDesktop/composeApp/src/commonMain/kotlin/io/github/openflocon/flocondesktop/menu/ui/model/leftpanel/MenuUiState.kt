package io.github.openflocon.flocondesktop.menu.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Settings
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen

data class LeftPanelState(
    val current: SubScreen,
    val sections: List<LeftPannelSection>,
    val bottomItems: List<LeftPanelItem>,
)

fun previewLeftPannelState(current: SubScreen) = LeftPanelState(
    current = current,
    bottomItems = listOf(
        LeftPanelItem(
            screen = SubScreen.Settings,
            icon = Icons.Outlined.Settings,
            text = "Settings",
            isSelected = selectedId == "Settings",
            isEnabled = true,
        ),
    ),
    sections = listOf(
        LeftPannelSection(
            title = "Network",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Http",
                    isSelected = selectedId == "Http",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Images,
                    icon = Icons.Outlined.Settings,
                    text = "Images",
                    isSelected = selectedId == "Images",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Grpc",
                    isSelected = selectedId == "Grpc",
                    isEnabled = true,
                ),
            ),
        ),
        LeftPannelSection(
            title = "Storage",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Database",
                    isSelected = selectedId == "Database",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.SharedPreferences,
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences",
                    isSelected = selectedId == "SharedPreferences",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Files,
                    icon = Icons.Outlined.Settings,
                    text = "Files",
                    isSelected = selectedId == "Files",
                    isEnabled = true,
                ),
            ),
        ),
        LeftPannelSection(
            title = "Data",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Dashboard,
                    icon = Icons.Outlined.Settings,
                    text = "Dashboard",
                    isSelected = selectedId == "Dashboard",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Tables,
                    icon = Icons.Outlined.Settings,
                    text = "Tables",
                    isSelected = selectedId == "Tables",
                    isEnabled = true,
                ),
            ),
        ),
    ),
)

package io.github.openflocon.flocondesktop.menu.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen

@Immutable
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
            text = "Settings"
        ),
    ),
    sections = listOf(
        LeftPannelSection(
            title = "Network",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Network"
                ),
                LeftPanelItem(
                    screen = SubScreen.Images,
                    icon = Icons.Outlined.Settings,
                    text = "Images"
                ),
                LeftPanelItem(
                    screen = SubScreen.Analytics,
                    icon = Icons.Outlined.Analytics,
                    text = "Analytics"
                ),
            ),
        ),
        LeftPannelSection(
            title = "Storage",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Database"
                ),
                LeftPanelItem(
                    screen = SubScreen.SharedPreferences,
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences"
                ),
                LeftPanelItem(
                    screen = SubScreen.Files,
                    icon = Icons.Outlined.Settings,
                    text = "Files"
                ),
            ),
        ),
        LeftPannelSection(
            title = "Data",
            items = listOf(
                LeftPanelItem(
                    screen = SubScreen.Dashboard,
                    icon = Icons.Outlined.Settings,
                    text = "Dashboard"
                ),
                LeftPanelItem(
                    screen = SubScreen.Tables,
                    icon = Icons.Outlined.Settings,
                    text = "Tables"
                ),
            ),
        ),
    ),
)

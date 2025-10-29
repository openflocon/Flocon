package io.github.openflocon.flocondesktop.app.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

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
            text = "Settings",
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
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Images,
                    icon = Icons.Outlined.Settings,
                    text = "Images",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Network,
                    icon = Icons.Outlined.Settings,
                    text = "Grpc",
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
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.SharedPreferences,
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Files,
                    icon = Icons.Outlined.Settings,
                    text = "Files",
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
                    isEnabled = true,
                ),
                LeftPanelItem(
                    screen = SubScreen.Tables,
                    icon = Icons.Outlined.Settings,
                    text = "Tables",
                    isEnabled = true,
                ),
            ),
        ),
    ),
)

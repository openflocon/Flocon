package io.github.openflocon.flocondesktop.main.ui.model.leftpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings

data class LeftPanelState(
    val sections: List<LeftPannelSection>,
    val bottomItems: List<LeftPanelItem>,
)

fun previewLeftPannelState(selectedId: String?) = LeftPanelState(
    bottomItems = listOf(
        LeftPanelItem(
            id = "Settings",
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
                    id = "Http",
                    icon = Icons.Outlined.Settings,
                    text = "Http",
                    isSelected = selectedId == "Http",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    id = "Images",
                    icon = Icons.Outlined.Settings,
                    text = "Images",
                    isSelected = selectedId == "Images",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    id = "Grpc",
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
                    id = "Database",
                    icon = Icons.Outlined.Settings,
                    text = "Database",
                    isSelected = selectedId == "Database",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    id = "SharedPreferences",
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences",
                    isSelected = selectedId == "SharedPreferences",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    id = "Files",
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
                    id = "Dashboard",
                    icon = Icons.Outlined.Settings,
                    text = "Dashboard",
                    isSelected = selectedId == "Dashboard",
                    isEnabled = true,
                ),
                LeftPanelItem(
                    id = "Tables",
                    icon = Icons.Outlined.Settings,
                    text = "Tables",
                    isSelected = selectedId == "Tables",
                    isEnabled = true,
                ),
            ),
        ),
    ),
)

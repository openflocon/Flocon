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
                ),
                LeftPanelItem(
                    id = "Images",
                    icon = Icons.Outlined.Settings,
                    text = "Images",
                    isSelected = selectedId == "Images",
                ),
                LeftPanelItem(
                    id = "Grpc",
                    icon = Icons.Outlined.Settings,
                    text = "Grpc",
                    isSelected = selectedId == "Grpc",
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
                ),
                LeftPanelItem(
                    id = "SharedPreferences",
                    icon = Icons.Outlined.Settings,
                    text = "SharedPreferences",
                    isSelected = selectedId == "SharedPreferences",
                ),
                LeftPanelItem(
                    id = "Files",
                    icon = Icons.Outlined.Settings,
                    text = "Files",
                    isSelected = selectedId == "Files",
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
                ),
                LeftPanelItem(
                    id = "Tables",
                    icon = Icons.Outlined.Settings,
                    text = "Tables",
                    isSelected = selectedId == "Tables",
                ),
            ),
        ),
    ),
)

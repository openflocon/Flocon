package io.github.openflocon.flocondesktop.main.ui.model.leftpanel

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.settings

data class LeftPanelState(
    val sections: List<LeftPannelSection>,
    val bottomItems: List<LeftPanelItem>,
)

fun previewLeftPannelState(selectedId: String?) = LeftPanelState(
    bottomItems = listOf(
        LeftPanelItem(
            id = "Settings",
            icon = Res.drawable.settings,
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
                    icon = Res.drawable.settings,
                    text = "Http",
                    isSelected = selectedId == "Http",
                ),
                LeftPanelItem(
                    id = "Images",
                    icon = Res.drawable.settings,
                    text = "Images",
                    isSelected = selectedId == "Images",
                ),
                LeftPanelItem(
                    id = "Grpc",
                    icon = Res.drawable.settings,
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
                    icon = Res.drawable.settings,
                    text = "Database",
                    isSelected = selectedId == "Database",
                ),
                LeftPanelItem(
                    id = "SharedPreferences",
                    icon = Res.drawable.settings,
                    text = "SharedPreferences",
                    isSelected = selectedId == "SharedPreferences",
                ),
                LeftPanelItem(
                    id = "Files",
                    icon = Res.drawable.settings,
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
                    icon = Res.drawable.settings,
                    text = "Dashboard",
                    isSelected = selectedId == "Dashboard",
                ),
                LeftPanelItem(
                    id = "Tables",
                    icon = Res.drawable.settings,
                    text = "Tables",
                    isSelected = selectedId == "Tables",
                ),
            ),
        ),
    ),
)

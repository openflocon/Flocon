package io.github.openflocon.flocondesktop.main.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TableView
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.main.ui.nav.model.MainNavigation

// Extension function to get the display name for each SubScreen
fun MainNavigation.displayName(): String = when (this) {
    MainNavigation.Analytics -> "Analytics"
    MainNavigation.Network -> "Network"
    MainNavigation.Database -> "Database"
    MainNavigation.Files -> "Files"
    MainNavigation.Tables -> "Tables"
    MainNavigation.Images -> "Images"
    MainNavigation.SharedPreferences -> "SharedPreferences"
    MainNavigation.Dashboard -> "Dashboard"
    MainNavigation.Settings -> "Settings"
    MainNavigation.Deeplinks -> "Deeplinks"
}

// Extension function to get the icon for each SubScreen
fun MainNavigation.icon(): ImageVector = when (this) {
    MainNavigation.Analytics -> Icons.Outlined.StackedBarChart
    MainNavigation.Network -> Icons.Filled.NetworkWifi
    MainNavigation.Database -> Icons.Outlined.DatasetLinked // Can't find database
    MainNavigation.Files -> Icons.Outlined.Folder
    MainNavigation.Tables -> Icons.Outlined.TableView
    MainNavigation.Images -> Icons.Outlined.Image
    MainNavigation.SharedPreferences -> Icons.Outlined.Storage
    MainNavigation.Settings -> Icons.Outlined.Settings
    MainNavigation.Dashboard -> Icons.Outlined.Dashboard
    MainNavigation.Deeplinks -> Icons.Filled.Link
}

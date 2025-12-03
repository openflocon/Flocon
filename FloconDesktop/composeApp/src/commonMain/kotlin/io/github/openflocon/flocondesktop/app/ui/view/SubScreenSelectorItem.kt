package io.github.openflocon.flocondesktop.app.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TableView
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

// Extension function to get the display name for each SubScreen
fun SubScreen.displayName(): String = when (this) {
    SubScreen.Analytics -> "Analytics"
    SubScreen.Network -> "Network"
    SubScreen.Database -> "Database"
    SubScreen.Files -> "Files"
    SubScreen.Tables -> "Tables"
    SubScreen.Images -> "Images"
    SubScreen.SharedPreferences -> "Preferences"
    SubScreen.Dashboard -> "Dashboard"
    SubScreen.Settings -> "Settings"
    SubScreen.Deeplinks -> "Deeplinks"
    SubScreen.CrashReporter -> "Crashes"
}

// Extension function to get the icon for each SubScreen
fun SubScreen.icon(): ImageVector = when (this) {
    SubScreen.Analytics -> Icons.Outlined.StackedBarChart
    SubScreen.Network -> Icons.Filled.NetworkWifi
    SubScreen.Database -> Icons.Outlined.DatasetLinked // Can't find database
    SubScreen.Files -> Icons.Outlined.Folder
    SubScreen.Tables -> Icons.Outlined.TableView
    SubScreen.Images -> Icons.Outlined.Image
    SubScreen.SharedPreferences -> Icons.Outlined.Storage
    SubScreen.Settings -> Icons.Outlined.Settings
    SubScreen.Dashboard -> Icons.Outlined.Dashboard
    SubScreen.Deeplinks -> Icons.Filled.Link
    SubScreen.CrashReporter -> Icons.Outlined.BugReport
}

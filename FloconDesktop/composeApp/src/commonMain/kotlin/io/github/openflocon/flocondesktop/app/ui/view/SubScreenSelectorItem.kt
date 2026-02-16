package io.github.openflocon.flocondesktop.app.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Commit
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DatasetLinked
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TableView
import androidx.compose.ui.graphics.vector.ImageVector
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_adb_commands
import flocondesktop.composeapp.generated.resources.menu_item_analytics
import flocondesktop.composeapp.generated.resources.menu_item_crashReporter
import flocondesktop.composeapp.generated.resources.menu_item_dashboard
import flocondesktop.composeapp.generated.resources.menu_item_database
import flocondesktop.composeapp.generated.resources.menu_item_deeplinks
import flocondesktop.composeapp.generated.resources.menu_item_files
import flocondesktop.composeapp.generated.resources.menu_item_images
import flocondesktop.composeapp.generated.resources.menu_item_network
import flocondesktop.composeapp.generated.resources.menu_item_settings
import flocondesktop.composeapp.generated.resources.menu_item_sharedPreferences
import flocondesktop.composeapp.generated.resources.menu_item_tables
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import org.jetbrains.compose.resources.StringResource

// Extension function to get the display name for each SubScreen
fun SubScreen.displayName(): StringResource = when (this) {
    SubScreen.Analytics -> Res.string.menu_item_analytics
    SubScreen.Network -> Res.string.menu_item_network
    SubScreen.Database -> Res.string.menu_item_database
    SubScreen.Files -> Res.string.menu_item_files
    SubScreen.Tables -> Res.string.menu_item_tables
    SubScreen.Images -> Res.string.menu_item_images
    SubScreen.SharedPreferences -> Res.string.menu_item_sharedPreferences
    SubScreen.Dashboard -> Res.string.menu_item_dashboard
    SubScreen.Settings -> Res.string.menu_item_settings
    SubScreen.Deeplinks -> Res.string.menu_item_deeplinks
    SubScreen.CrashReporter -> Res.string.menu_item_crashReporter
    SubScreen.Commands -> Res.string.menu_item_adb_commands
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
    SubScreen.Commands -> Icons.Outlined.Commit
}

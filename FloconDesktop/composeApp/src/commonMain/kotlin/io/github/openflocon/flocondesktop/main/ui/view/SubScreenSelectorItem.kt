package io.github.openflocon.flocondesktop.main.ui.view

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.analytics
import flocondesktop.composeapp.generated.resources.dashboard
import flocondesktop.composeapp.generated.resources.database
import flocondesktop.composeapp.generated.resources.deeplinks
import flocondesktop.composeapp.generated.resources.files
import flocondesktop.composeapp.generated.resources.grpc
import flocondesktop.composeapp.generated.resources.images
import flocondesktop.composeapp.generated.resources.network
import flocondesktop.composeapp.generated.resources.settings
import flocondesktop.composeapp.generated.resources.sharedpreference
import flocondesktop.composeapp.generated.resources.tables
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import org.jetbrains.compose.resources.DrawableResource

// Extension function to get the display name for each SubScreen
fun SubScreen.displayName(): String = when (this) {
    SubScreen.Analytics -> "Analytics"
    SubScreen.Network -> "Network"
    SubScreen.Database -> "Database"
    SubScreen.Files -> "Files"
    SubScreen.Tables -> "Tables"
    SubScreen.Images -> "Images"
    SubScreen.GRPC -> "gRPC"
    SubScreen.SharedPreferences -> "SharedPreferences"
    SubScreen.Dashboard -> "Dashboard"
    SubScreen.Settings -> "Settings"
    SubScreen.Deeplinks -> "Deeplinks"
}

// Extension function to get the icon for each SubScreen
fun SubScreen.icon(): DrawableResource = when (this) {
    SubScreen.Analytics -> Res.drawable.analytics
    SubScreen.Network -> Res.drawable.network
    SubScreen.Database -> Res.drawable.database
    SubScreen.Files -> Res.drawable.files
    SubScreen.Tables -> Res.drawable.tables
    SubScreen.GRPC -> Res.drawable.grpc
    SubScreen.Images -> Res.drawable.images
    SubScreen.SharedPreferences -> Res.drawable.sharedpreference
    SubScreen.Settings -> Res.drawable.settings
    SubScreen.Dashboard -> Res.drawable.dashboard
    SubScreen.Deeplinks -> Res.drawable.deeplinks
}

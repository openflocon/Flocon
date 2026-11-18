package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceTypeUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTintedIcon

@Composable
internal fun PreferenceTypeIcon(
    type: PreferenceTypeUiModel,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
) {
    val colors = FloconTheme.colors
    val (icon, color) = when (type) {
        PreferenceTypeUiModel.SharedPreferences -> SharedPreferencesIcon to colors.catTable
        PreferenceTypeUiModel.DataStore -> DataStoreIcon to colors.catDatabase
        PreferenceTypeUiModel.UserDefaults -> UserDefaultsIcon to colors.catNetwork
        PreferenceTypeUiModel.Keychain -> KeychainIcon to colors.accent
        PreferenceTypeUiModel.JavaPreferences -> JavaPreferencesIcon to colors.catAccessibility
        PreferenceTypeUiModel.LocalStorage -> LocalStorageIcon to colors.catAnalytics
        PreferenceTypeUiModel.Custom -> CustomIcon to colors.catAdb
    }
    FloconTintedIcon(
        imageVector = icon,
        color = color,
        size = size,
        modifier = modifier,
    )
}

// line glyphs on a 24x24 grid, stroked (the tint is applied by the icon)
private fun strokeIcon(name: String, vararg paths: String): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    paths.forEach { path ->
        addPath(
            pathData = addPathNodes(path),
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
}.build()

// key / value list
private val SharedPreferencesIcon = strokeIcon(
    "SharedPreferences",
    "M3.5 6a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0 -3 0",
    "M3.5 12a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0 -3 0",
    "M3.5 18a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0 -3 0",
    "M10 6h10M10 12h10M10 18h7",
)

// database
private val DataStoreIcon = strokeIcon(
    "DataStore",
    "M5 5a7 3 0 1 0 14 0a7 3 0 1 0 -14 0",
    "M5 5v14c0 1.7 3.1 3 7 3s7-1.3 7-3V5",
    "M5 12c0 1.7 3.1 3 7 3s7-1.3 7-3",
)

// settings sliders
private val UserDefaultsIcon = strokeIcon(
    "UserDefaults",
    "M4 7h10M18 7h2M4 17h4M12 17h8",
    "M14 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
    "M8 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0",
)

// key
private val KeychainIcon = strokeIcon(
    "Keychain",
    "M4 15a4 4 0 1 0 8 0a4 4 0 1 0 -8 0",
    "M10.8 12.2L19 4M16 7l2 2M14 9l2 2",
)

// coffee cup
private val JavaPreferencesIcon = strokeIcon(
    "JavaPreferences",
    "M4 9h12v5a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5z",
    "M16 11h1.5a2.5 2.5 0 0 1 0 5H16",
    "M8 3v3M12 3v3",
)

// browser window
private val LocalStorageIcon = strokeIcon(
    "LocalStorage",
    "M5 4h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
    "M3 9h18",
    "M6.5 6.5h0.01M9 6.5h0.01",
)

// shapes: any other storage
private val CustomIcon = strokeIcon(
    "Custom",
    "M5 4h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z",
    "M14 7a3 3 0 1 0 6 0a3 3 0 1 0 -6 0",
    "M7 14l3.5 6h-7z",
    "M14 17a3 3 0 1 0 6 0a3 3 0 1 0 -6 0",
)

@Preview
@Composable
private fun PreferenceTypeIconPreview() {
    FloconTheme {
        Row {
            PreferenceTypeUiModel.entries.forEach { PreferenceTypeIcon(it) }
        }
    }
}

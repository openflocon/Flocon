package io.github.openflocon.flocon.myapplication.multi.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Stroke icons (24x24, 1.8 stroke), drawn from SVG path data, so no icon library is needed. */
object SampleIcons {
    val Back = icon("back", "M15 5l-7 7 7 7")
    val Chevron = icon("chevron", "M9 5l7 7-7 7")
    val Network = icon("network", "M7 4v13M7 17l-3-3M7 17l3-3M17 20V7M17 7l-3 3M17 7l3 3")
    val Database = icon(
        "database",
        "M5 6c0-1.7 3.1-3 7-3s7 1.3 7 3-3.1 3-7 3-7-1.3-7-3z",
        "M5 6v6c0 1.7 3.1 3 7 3s7-1.3 7-3V6",
        "M5 12v6c0 1.7 3.1 3 7 3s7-1.3 7-3v-6",
    )
    val Analytics = icon("analytics", "M4 20V10M10 20V4M16 20v-7M22 20H2")
    val FeatureFlags = icon("flags", "M5 21V4M5 4h11l-2 4 2 4H5")
    val Preferences = icon(
        "preferences",
        "M4 7a3 3 0 0 1 3-3h10a3 3 0 0 1 3 3v10a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3z",
        "M4 10h16M10 4v16",
    )
    val Files = icon("files", "M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z")
    val Dashboard = icon(
        "dashboard",
        "M3 5.5A1.5 1.5 0 0 1 4.5 4h15A1.5 1.5 0 0 1 21 5.5v3A1.5 1.5 0 0 1 19.5 10h-15A1.5 1.5 0 0 1 3 8.5z",
        "M3 15.5A1.5 1.5 0 0 1 4.5 14h15a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 18.5z",
    )
    val Deeplinks = icon(
        "deeplinks",
        "M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1",
        "M14 10a4 4 0 0 0-5.7 0l-3 3A4 4 0 0 0 11 18.7l1-1",
    )
    val Images = icon(
        "images",
        "M5 4h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
        "M21 16l-5-5-8 8",
        "M9 8.5a1 1 0 1 0 0 .01",
    )
    val Crash = icon("crash", "M12 3L2 20h20z", "M12 10v5M12 18v.01")

    private fun icon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            paths.forEach { data ->
                addPath(
                    pathData = PathParser().parsePathString(data).toNodes(),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}

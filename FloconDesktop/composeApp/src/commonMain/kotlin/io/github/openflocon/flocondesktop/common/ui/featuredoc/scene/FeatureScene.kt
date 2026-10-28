package io.github.openflocon.flocondesktop.common.ui.featuredoc.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPixelScene
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

// where the mascot's own grid starts in the scene, the object it plays with is on its right
private const val MASCOT_X = 2
private const val MASCOT_Y = 4

// pixel disc behind the scene, like the backdrop of the illustrations: (x, y, width) of each row
private const val BACKDROP_CENTER_X = 25
private const val BACKDROP_CENTER_Y = 16
private const val BACKDROP_RADIUS_X = 19
private const val BACKDROP_RADIUS_Y = 15
private val BackdropRows: List<Triple<Int, Int, Int>> = (-BACKDROP_RADIUS_Y..BACKDROP_RADIUS_Y).mapNotNull { row ->
    val dy = (row + 0.5f) / BACKDROP_RADIUS_Y
    if (abs(dy) > 1f) return@mapNotNull null
    val halfWidth = (BACKDROP_RADIUS_X * sqrt(1f - dy * dy)).roundToInt()
    if (halfWidth == 0) null else Triple(BACKDROP_CENTER_X - halfWidth, BACKDROP_CENTER_Y + row, halfWidth * 2)
}

/**
 * Looping 8-bit scene of a page: the mascot using the object of the feature.
 * [draw] works in the mascot's grid (see [io.github.openflocon.library.designsystem.components.drawMascotSitting]).
 */
@Immutable
internal class FeatureSceneSpec(
    val frames: ImmutableList<Int>,
    val frameMs: Long,
    val draw: FloconPixelScope.(frame: Int) -> Unit,
)

/** The scene of the empty state of [this] page, null when it has none. */
internal fun FeatureDoc.scene(): FeatureSceneSpec? = when (this) {
    FeatureDoc.Network -> NetworkScene
    FeatureDoc.Images -> ImagesScene
    FeatureDoc.Database -> DatabaseScene
    FeatureDoc.SharedPreferences -> PreferencesScene
    FeatureDoc.Files -> FilesScene
    FeatureDoc.Dashboard -> DashboardScene
    FeatureDoc.Analytics -> AnalyticsScene
    FeatureDoc.Tables -> TablesScene
    FeatureDoc.Deeplinks -> DeeplinksScene
    FeatureDoc.FeatureFlags -> FeatureFlagsScene
    FeatureDoc.Timeline -> TimelineScene
    FeatureDoc.CrashReporter -> CrashReporterScene
    FeatureDoc.AdbCommander -> AdbCommanderWifiScene
}

@Composable
internal fun FeatureScene(
    scene: FeatureSceneSpec,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val backdrop = colors.bg300
    val sparkle = colors.accent
    val faintSparkle = colors.inkFaint
    FloconPixelScene(
        frames = scene.frames,
        frameMs = scene.frameMs,
        modifier = modifier,
    ) { frame ->
        BackdropRows.forEach { (x, y, width) -> pixels(x, y, width, 1, backdrop) }
        drawSparkle(5, 5, sparkle, large = true)
        drawSparkle(46, 7, faintSparkle)
        drawSparkle(4, 27, faintSparkle)
        scene.draw(offset(MASCOT_X, MASCOT_Y), frame)
    }
}

private fun FloconPixelScope.drawSparkle(x: Int, y: Int, color: Color, large: Boolean = false) {
    pixels(x, y - 1, 1, 3, color)
    pixels(x - 1, y, 3, 1, color)
    if (large) {
        pixels(x, y - 2, 1, 1, color)
        pixels(x, y + 2, 1, 1, color)
        pixels(x - 2, y, 1, 1, color)
        pixels(x + 2, y, 1, 1, color)
    }
}

@Preview
@Composable
private fun FeatureScenePreview() {
    FloconTheme {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)) {
            FeatureDoc.entries.mapNotNull { it.scene() }.forEach {
                FeatureScene(scene = it, modifier = Modifier.size(width = 200.dp, height = 128.dp))
            }
        }
    }
}

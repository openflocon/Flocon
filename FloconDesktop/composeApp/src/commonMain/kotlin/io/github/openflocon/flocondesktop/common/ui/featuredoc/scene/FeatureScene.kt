package io.github.openflocon.flocondesktop.common.ui.featuredoc.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPixelScene
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import kotlinx.collections.immutable.ImmutableList

// where the mascot's own grid starts in the scene, the object it plays with is on its right
private const val MASCOT_X = 2
private const val MASCOT_Y = 4

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
    FeatureDoc.AdbCommander -> null
}

@Composable
internal fun FeatureScene(
    scene: FeatureSceneSpec,
    modifier: Modifier = Modifier,
) {
    FloconPixelScene(
        frames = scene.frames,
        frameMs = scene.frameMs,
        modifier = modifier,
    ) { frame ->
        scene.draw(offset(MASCOT_X, MASCOT_Y), frame)
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

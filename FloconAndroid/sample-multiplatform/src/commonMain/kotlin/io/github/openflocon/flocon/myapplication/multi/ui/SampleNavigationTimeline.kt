package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.timeline.timelinePlugin

/** A stable, readable name for the timeline: "home", "network", "feature_flags"... */
internal val SampleRoute.timelineName: String
    get() = when (this) {
        SampleRoute.Home -> "home"
        SampleRoute.Network -> "network"
        SampleRoute.Database -> "database"
        SampleRoute.Analytics -> "analytics"
        SampleRoute.Accessibility -> "accessibility"
        SampleRoute.WebView -> "web_view"
        SampleRoute.FeatureFlags -> "feature_flags"
        SampleRoute.Preferences -> "preferences"
        SampleRoute.Files -> "files"
        SampleRoute.Dashboard -> "dashboard"
        SampleRoute.Deeplinks -> "deeplinks"
        SampleRoute.Images -> "images"
        SampleRoute.Timeline -> "timeline"
        SampleRoute.Crash -> "crash"
        SampleRoute.Connection -> "connection"
    }

/**
 * Example of a navigation listener for the Flocon timeline: every change of the top of the back
 * stack is logged once, here, instead of in each screen. Routes of this sample have no arguments,
 * with Navigation 3 keys that carry some, add them to the params.
 */
@Composable
internal fun LogNavigationToTimeline(backStack: SnapshotStateList<SampleRoute>) {
    val current = backStack.lastOrNull() ?: return
    var previous by remember { mutableStateOf<SampleRoute?>(null) }
    LaunchedEffect(current) {
        Flocon.timelinePlugin.logNavigation(
            value = current.timelineName,
            params = buildMap {
                previous?.let { put("from", it.timelineName) }
                put("backStackSize", backStack.size.toString())
            },
        )
        previous = current
    }
}

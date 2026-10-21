package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Flat, theme aware illustration dedicated to each page of the desktop app.
 */
@Composable
fun FeatureIllustration(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
) {
    IllustrationCanvas(modifier = modifier) { colors ->
        when (doc) {
            FeatureDoc.Network -> drawNetwork(colors)
            FeatureDoc.Images -> drawImages(colors)
            FeatureDoc.Database -> drawDatabase(colors)
            FeatureDoc.SharedPreferences -> drawPreferences(colors)
            FeatureDoc.Files -> drawFiles(colors)
            FeatureDoc.Dashboard -> drawDashboard(colors)
            FeatureDoc.Analytics -> drawAnalytics(colors)
            FeatureDoc.Tables -> drawTables(colors)
            FeatureDoc.Deeplinks -> drawDeeplinks(colors)
            FeatureDoc.FeatureFlags -> drawFeatureFlags(colors)
            FeatureDoc.Timeline -> drawTimeline(colors)
            FeatureDoc.CrashReporter -> drawCrashReporter(colors)
            FeatureDoc.AdbCommander -> drawAdbCommander(colors)
        }
    }
}

@Preview
@Composable
private fun FeatureIllustrationsPreview() {
    FloconTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureDoc.entries.forEach {
                FeatureIllustration(doc = it, modifier = Modifier.size(width = 160.dp, height = 120.dp))
            }
        }
    }
}

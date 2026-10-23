package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleCategory
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleIcons

/** One screen per plugin of the SDK. */
enum class SampleScreen(
    val title: String,
    val subtitle: String,
    val category: SampleCategory,
    val icon: ImageVector,
    val platforms: Set<SamplePlatform> = SamplePlatform.All,
) {
    Network("Network", "HTTP, WebSocket, GraphQL, gRPC", SampleCategory.Network, SampleIcons.Network),
    Database("Database", "Room 2, Room 3, in-memory", SampleCategory.Database, SampleIcons.Database),
    Analytics("Analytics", "Events and properties", SampleCategory.Analytics, SampleIcons.Analytics),
    FeatureFlags("Feature flags", "Toggle and observe", SampleCategory.FeatureFlags, SampleIcons.FeatureFlags),
    Preferences("Preferences", "Custom store, DataStore, SharedPreferences", SampleCategory.Neutral, SampleIcons.Preferences),
    Files("Files", "Files the desktop can browse", SampleCategory.Neutral, SampleIcons.Files, SamplePlatform.NotWeb),
    Dashboard("Dashboard & Tables", "Live values and table rows", SampleCategory.Table, SampleIcons.Dashboard),
    Deeplinks("Deeplinks", "Static and runtime links", SampleCategory.Deeplink, SampleIcons.Deeplinks),
    Images("Images", "A scrolling list of remote images", SampleCategory.Neutral, SampleIcons.Images),
    Crash("Crash reporter", "Throw a test crash", SampleCategory.Crash, SampleIcons.Crash, SamplePlatform.NotWeb),
}

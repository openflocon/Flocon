package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.components.ScreenScaffold
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

/** The frame shared by every plugin screen: the top bar of [screen], then [content]. */
@Composable
internal fun PluginScreen(
    screen: SampleScreen,
    onBack: () -> Unit,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    ScreenScaffold(
        title = screen.title,
        subtitle = screen.subtitle,
        category = screen.category,
        icon = screen.icon,
        onBack = onBack,
        scrollable = scrollable,
        content = content,
    )
}

/** The note displayed when some rows of a screen are greyed on the current platform. */
@Composable
internal fun PlatformNote(screen: SampleScreen) {
    if (SampleFeatures.isAnyUnavailable(screen)) {
        InfoCard(
            text = "Not on every platform. Greyed rows are not supported on ${SampleFeatures.platformName}, " +
                "the reason is shown under their title.",
        )
    }
}

@Composable
fun NetworkScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Network, onBack) {
    ActionGroup(
        "HTTP",
        listOf(
            SampleFeature.KtorGet,
            SampleFeature.KtorPost,
            SampleFeature.KtorNotFound,
            SampleFeature.OkHttp,
            SampleFeature.OkHttpGzip,
        ),
    )
    ActionGroup("Realtime & APIs", listOf(SampleFeature.WebSocket, SampleFeature.GraphQl, SampleFeature.Grpc))
    PlatformNote(SampleScreen.Network)
}

@Composable
fun DatabaseScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Database, onBack) {
    ActionGroup(
        "Insert rows",
        listOf(
            SampleFeature.DatabaseRoom3,
            SampleFeature.DatabaseFood,
            SampleFeature.DatabaseRoom2,
            SampleFeature.DatabaseInMemory,
        ),
    )
    InfoCard(
        text = "Databases are registered with Flocon at startup (dogs, food, ...). Open them in Flocon > Database " +
            "to run queries and watch the inserted rows.",
        tint = SampleTheme.colors.accent,
    )
    PlatformNote(SampleScreen.Database)
}

@Composable
fun FeatureFlagsScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.FeatureFlags, onBack) {
    ActionGroup("Flags", listOf(SampleFeature.FeatureFlags))
    InfoCard(
        text = "Flags are registered when the app starts. Change their state or variant from Flocon > Feature flags, " +
            "or register them again here to reset them.",
        tint = SampleTheme.colors.accent,
    )
}

@Composable
fun FilesScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Files, onBack) {
    ActionGroup("Files", listOf(SampleFeature.Files))
    PlatformNote(SampleScreen.Files)
}

@Composable
fun DashboardScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Dashboard, onBack) {
    ActionGroup("Dashboard", listOf(SampleFeature.Dashboard))
    ActionGroup("Tables", listOf(SampleFeature.Table))
}

@Composable
fun DeeplinksScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Deeplinks, onBack) {
    ActionGroup("Runtime", listOf(SampleFeature.Deeplink))
    InfoCard(
        text = "flocon://home is registered at startup. Open the links from Flocon > Deeplinks, " +
            "the app receives them like any other deeplink.",
        tint = SampleTheme.colors.accent,
    )
}

@Composable
fun CrashScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Crash, onBack) {
    ActionGroup("Crash", listOf(SampleFeature.CrashReporter))
    InfoCard(
        text = "The app will close. Relaunch it, then open Flocon > Crash reporter to read the stack trace.",
        tint = SampleTheme.colors.error,
    )
    PlatformNote(SampleScreen.Crash)
}

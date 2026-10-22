package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.components.ScreenScaffold

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

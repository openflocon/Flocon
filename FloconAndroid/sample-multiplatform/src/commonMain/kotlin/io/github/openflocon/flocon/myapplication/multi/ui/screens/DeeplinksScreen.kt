package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun DeeplinksScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Deeplinks, onBack) {
    ActionGroup("Runtime", listOf(SampleFeature.Deeplink))
    InfoCard(
        text = "flocon://home is registered at startup. Open the links from Flocon > Deeplinks, " +
            "the app receives them like any other deeplink.",
        tint = SampleTheme.colors.accent,
    )
}

package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun FeatureFlagsScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.FeatureFlags, onBack) {
    ActionGroup("Flags", listOf(SampleFeature.FeatureFlags))
    InfoCard(
        text = "Flags are registered when the app starts and sent to Flocon Desktop, where you can inspect their state, " +
            "variant and payload in Feature flags. The view is read only: register them again here to send them again.",
        tint = SampleTheme.colors.accent,
    )
}

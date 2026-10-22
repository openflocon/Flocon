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
        text = "Flags are registered when the app starts. Change their state or variant from Flocon > Feature flags, " +
            "or register them again here to reset them.",
        tint = SampleTheme.colors.accent,
    )
}

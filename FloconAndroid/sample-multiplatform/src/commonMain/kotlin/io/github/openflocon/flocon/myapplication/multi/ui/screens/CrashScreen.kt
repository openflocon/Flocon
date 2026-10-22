package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun CrashScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Crash, onBack) {
    ActionGroup("Crash", listOf(SampleFeature.CrashReporter))
    InfoCard(
        text = "The app will close. Relaunch it, then open Flocon > Crash reporter to read the stack trace.",
        tint = SampleTheme.colors.error,
    )
    PlatformNote(SampleScreen.Crash)
}

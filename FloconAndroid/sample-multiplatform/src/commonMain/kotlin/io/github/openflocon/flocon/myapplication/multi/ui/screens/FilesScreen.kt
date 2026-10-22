package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup

@Composable
fun FilesScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Files, onBack) {
    ActionGroup("Files", listOf(SampleFeature.Files))
    PlatformNote(SampleScreen.Files)
}

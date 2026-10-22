package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup

@Composable
fun DashboardScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Dashboard, onBack) {
    ActionGroup("Dashboard", listOf(SampleFeature.Dashboard))
    ActionGroup("Tables", listOf(SampleFeature.Table))
}

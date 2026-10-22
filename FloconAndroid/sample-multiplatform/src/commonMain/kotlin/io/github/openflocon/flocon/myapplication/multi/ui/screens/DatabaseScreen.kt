package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.runtime.Composable
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

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

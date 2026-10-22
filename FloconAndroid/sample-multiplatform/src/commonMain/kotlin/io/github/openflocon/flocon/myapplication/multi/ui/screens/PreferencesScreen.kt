package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.preferences.SamplePreferences
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.AccentButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.GhostButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun PreferencesScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Preferences, onBack) {
    val colors = SampleTheme.colors

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Custom store",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("counter", style = MaterialTheme.typography.labelMedium, color = colors.inkMuted)
                    Text(
                        SamplePreferences.counter.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.ink,
                    )
                }
                GhostButton("−", onClick = { SamplePreferences.counter-- })
                AccentButton("+", onClick = { SamplePreferences.counter++ })
            }
            Text(
                "An in-memory store available on every platform. Edit \"counter\" from Flocon > Preferences " +
                    "and the value here follows.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
        }
    }

    ActionGroup("DataStore", listOf(SampleFeature.DataStore))
    InfoCard(
        text = "On Android, the SharedPreferences files (user_pref, settings_pref) are detected automatically " +
            "and listed in Flocon > Preferences.",
        tint = colors.accent,
    )
    PlatformNote(SampleScreen.Preferences)
}

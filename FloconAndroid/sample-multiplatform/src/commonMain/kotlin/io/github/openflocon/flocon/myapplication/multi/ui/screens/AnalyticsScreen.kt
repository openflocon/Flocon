package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.analytics.analyticsPlugin
import io.github.openflocon.flocon.analytics.model.analyticsProperty
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.AccentButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun AnalyticsScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Analytics, onBack) {
    val colors = SampleTheme.colors
    var eventName by remember { mutableStateOf("clicked user") }
    var sentCount by remember { mutableStateOf(0) }

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "Compose an event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "Sent with the properties userId = 1024 and username = florent, from the sender \"sample\".",
                style = MaterialTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
            AccentButton(
                text = if (sentCount == 0) "Send event" else "Send event ($sentCount sent)",
                enabled = eventName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Flocon.analyticsPlugin.sender("sample").log(
                        eventName.trim(),
                        "userId" analyticsProperty "1024",
                        "username" analyticsProperty "florent",
                    )
                    sentCount++
                },
            )
        }
    }

    ActionGroup("Presets", listOf(SampleFeature.AnalyticsTable, SampleFeature.AnalyticsEvents))
}

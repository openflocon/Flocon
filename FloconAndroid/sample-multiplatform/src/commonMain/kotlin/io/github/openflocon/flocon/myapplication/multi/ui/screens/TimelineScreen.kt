package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import io.github.openflocon.flocon.myapplication.multi.timeline.Billing
import io.github.openflocon.flocon.myapplication.multi.timeline.logBilling
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.AccentButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.GhostButton
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme
import io.github.openflocon.flocon.timeline.FloconTimelinePlugin
import io.github.openflocon.flocon.timeline.model.FloconTimelineLevel
import io.github.openflocon.flocon.timeline.timelinePlugin

@Composable
fun TimelineScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Timeline, onBack) {
    ReservedTypesCard()
    TaggedLogCard()
    CustomTypeCard()
    BurstCard()
}

@Composable
private fun SectionCard(title: String, description: String, content: @Composable () -> Unit) {
    val colors = SampleTheme.colors
    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            Text(description, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
            content()
        }
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
    )
}

@Composable
private fun ReservedTypesCard() {
    var value by remember { mutableStateOf("checkout") }
    SectionCard(
        "Built-in types",
        "Navigation, user action and lifecycle events carry a text value.",
    ) {
        Field("Value", value, { value = it }, Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AccentButton("Navigation", { Flocon.timelinePlugin.logNavigation(value) }, Modifier.weight(1f), value.isNotBlank())
            AccentButton("User action", { Flocon.timelinePlugin.logUserAction(value) }, Modifier.weight(1f), value.isNotBlank())
            AccentButton("Lifecycle", { Flocon.timelinePlugin.logLifecycle(value) }, Modifier.weight(1f), value.isNotBlank())
        }
    }
}

@Composable
private fun TaggedLogCard() {
    var tag by remember { mutableStateOf("payment") }
    var value by remember { mutableStateOf("card declined") }
    var level by remember { mutableStateOf(FloconTimelineLevel.Warning) }
    var key1 by remember { mutableStateOf("orderId") }
    var value1 by remember { mutableStateOf("42") }
    var key2 by remember { mutableStateOf("") }
    var value2 by remember { mutableStateOf("") }

    SectionCard("Tagged log", "The tag is the type of the event, the level colors it in Flocon.") {
        Field("Tag", tag, { tag = it }, Modifier.fillMaxWidth())
        Field("Value", value, { value = it }, Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FloconTimelineLevel.entries.forEach {
                val select = { level = it }
                if (it == level) {
                    AccentButton(it.name, select, Modifier.weight(1f))
                } else {
                    GhostButton(it.name, select, Modifier.weight(1f))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Field("Param 1 key", key1, { key1 = it }, Modifier.weight(1f))
            Field("Param 1 value", value1, { value1 = it }, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Field("Param 2 key", key2, { key2 = it }, Modifier.weight(1f))
            Field("Param 2 value", value2, { value2 = it }, Modifier.weight(1f))
        }
        AccentButton(
            text = "Send tagged log",
            enabled = tag.isNotBlank() && value.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val params = listOf(key1 to value1, key2 to value2)
                    .filter { (key, _) -> key.isNotBlank() }
                    .toMap()
                Flocon.timelinePlugin.log(tag.trim(), value, params, level)
            },
        )
    }
}

@Composable
private fun CustomTypeCard() {
    SectionCard(
        "Custom type",
        "Billing = FloconTimelineType(\"billing\"), logged with the extension logBilling(...).",
    ) {
        AccentButton(
            text = "Log billing",
            modifier = Modifier.fillMaxWidth(),
            onClick = { Flocon.timelinePlugin.logBilling("subscription renewed", mapOf("plan" to "pro", "amount" to "9.99")) },
        )
    }
}

@Composable
private fun BurstCard() {
    SectionCard(
        "Burst",
        "Sends 50 logs of mixed types and levels, to see the graph fill up. Flocon keeps the last 20 000 events.",
    ) {
        AccentButton(
            text = "Send 50 logs",
            modifier = Modifier.fillMaxWidth(),
            onClick = { sendBurst(Flocon.timelinePlugin, 50) },
        )
    }
}

private fun sendBurst(timeline: FloconTimelinePlugin, count: Int) {
    val levels = FloconTimelineLevel.entries
    repeat(count) { i ->
        val params = mapOf("index" to i.toString())
        when (i % 5) {
            0 -> timeline.logNavigation("screen_$i", params)
            1 -> timeline.logUserAction("tap_$i", params)
            2 -> timeline.logLifecycle(if (i % 2 == 0) "foreground" else "background", params)
            3 -> timeline.logBilling("charge_$i", params)
            else -> timeline.log("burst", "log_$i", params, levels[i % levels.size])
        }
    }
}

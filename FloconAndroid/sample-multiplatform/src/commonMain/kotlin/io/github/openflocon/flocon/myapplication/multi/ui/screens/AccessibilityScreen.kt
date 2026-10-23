package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.AccentButton
import io.github.openflocon.flocon.myapplication.multi.ui.components.ActionGroup
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.InfoCard
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleIcons
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

@Composable
fun AccessibilityScreen(onBack: () -> Unit) = PluginScreen(SampleScreen.Accessibility, onBack) {
    val colors = SampleTheme.colors
    var taps by remember { mutableStateOf(0) }

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "Tap the elements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            Text(
                "Each tap sends a CLICK event ($taps so far). The red icon has no label: the desktop flags it.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
            AccentButton(text = "Add to cart", onClick = { taps++ }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    SampleIcons.Accessibility,
                    contentDescription = "Accessibility settings",
                    tint = colors.accent,
                    modifier = Modifier
                        .clickable(role = Role.Button) { taps++ }
                        .padding(8.dp)
                        .size(28.dp),
                )
                Icon(
                    SampleIcons.Accessibility,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier
                        .clickable(role = Role.Button) { taps++ }
                        .padding(8.dp)
                        .size(28.dp),
                )
            }
        }
    }

    ActionGroup("Announcement", listOf(SampleFeature.Accessibility))
    InfoCard(
        text = "On Android, turn on TalkBack to also get the focus, announcement and window events. Taps are captured without it.",
        tint = colors.inkMuted,
    )
    PlatformNote(SampleScreen.Accessibility)
}

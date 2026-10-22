package io.github.openflocon.flocon.myapplication.multi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.myapplication.multi.ui.SampleScreen
import io.github.openflocon.flocon.myapplication.multi.ui.components.CategoryIcon
import io.github.openflocon.flocon.myapplication.multi.ui.components.Card
import io.github.openflocon.flocon.myapplication.multi.ui.components.Pill
import io.github.openflocon.flocon.myapplication.multi.ui.components.SectionLabel
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleIcons
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

private val mainScreens = listOf(
    SampleScreen.Network,
    SampleScreen.Database,
    SampleScreen.Analytics,
    SampleScreen.FeatureFlags,
)

private val otherScreens = SampleScreen.entries - mainScreens.toSet()

@Composable
fun HomeScreen(onOpen: (SampleScreen) -> Unit) {
    val colors = SampleTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "FLOCON SAMPLE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.inkMuted,
                )
                Pill(text = SampleFeatures.platformName, color = colors.accent)
            }
            Text(
                "Try every plugin,\none screen each",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.ink,
            )
            Text(
                "Trigger an action here, find it in Flocon Desktop.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.inkMuted,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            mainScreens.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { screen ->
                        PluginTile(screen, onClick = { onOpen(screen) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel("More plugins")
            Card {
                otherScreens.forEachIndexed { index, screen ->
                    if (index > 0) HorizontalDivider(color = colors.line)
                    PluginRow(screen, onClick = { onOpen(screen) })
                }
            }
        }
    }
}

@Composable
private fun PluginTile(screen: SampleScreen, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SampleTheme.colors
    Card(modifier = modifier.clickable(role = Role.Button, onClick = onClick)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CategoryIcon(screen.icon, screen.category, size = 44.dp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    screen.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                )
                Text(screen.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
            }
        }
    }
}

@Composable
private fun PluginRow(screen: SampleScreen, onClick: () -> Unit) {
    val colors = SampleTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CategoryIcon(screen.icon, screen.category, size = 36.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                screen.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            )
            Text(screen.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
        }
        Icon(SampleIcons.Chevron, contentDescription = null, tint = colors.inkFaint, modifier = Modifier.size(18.dp))
    }
}

package io.github.openflocon.flocon.myapplication.multi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.ActionResult
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeature
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatureStatus
import io.github.openflocon.flocon.myapplication.multi.ui.SamplePlatform
import io.github.openflocon.flocon.myapplication.multi.ui.SampleFeatures
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleCategory
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleIcons
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme
import kotlinx.coroutines.launch

/** Top bar with a back button, a title and the icon of the plugin, then the content of the screen. */
@Composable
fun ScreenScaffold(
    title: String,
    subtitle: String,
    category: SampleCategory,
    icon: ImageVector,
    onBack: () -> Unit,
    scrollable: Boolean = true,
    platforms: Set<SamplePlatform>? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = SampleTheme.colors
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHigh)
                    .border(1.dp, colors.line, CircleShape)
                    .clickable(role = Role.Button, onClickLabel = "Back", onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(SampleIcons.Back, contentDescription = "Back", tint = colors.ink, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.ink)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
            }
            CategoryIcon(icon = icon, category = category, size = 44.dp)
        }
        if (platforms != null) {
            PlatformChips(
                supported = platforms,
                current = SampleFeatures.platform,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            )
        }
        val body = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).then(body),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (scrollable) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    content = content,
                )
            } else {
                content()
            }
        }
    }
}

@Composable
fun CategoryIcon(icon: ImageVector, category: SampleCategory, size: androidx.compose.ui.unit.Dp = 36.dp) {
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(size / 3.5f)).background(category.soft),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = category.color, modifier = Modifier.size(size / 2))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = SampleTheme.colors.inkFaint,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = SampleTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(BorderStroke(1.dp, colors.line), RoundedCornerShape(16.dp)),
        content = content,
    )
}

@Composable
fun AccentButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val colors = SampleTheme.colors
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .widthIn(min = 76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) colors.accent else colors.surfaceHighest)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) colors.onAccent else colors.inkFaint,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/** A white-on-dark secondary button, for the less important actions. */
@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SampleTheme.colors
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceHigh)
            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, color = SampleTheme.colors.ink, style = MaterialTheme.typography.labelLarge)
    }
}

/** A titled card with one [ActionRow] per feature. */
@Composable
fun ActionGroup(title: String, features: List<SampleFeature>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(title)
        Card {
            features.forEachIndexed { index, feature ->
                if (index > 0) HorizontalDivider(color = SampleTheme.colors.line)
                ActionRow(feature)
            }
        }
    }
}

/**
 * One feature of the SDK: a title, what it does, and a button. Greyed with the reason when the
 * platform doesn't support it, and the result (duration or error) of the last run is displayed.
 */
@Composable
fun ActionRow(feature: SampleFeature) {
    val colors = SampleTheme.colors
    val scope = rememberCoroutineScope()
    val status = SampleFeatures.status(feature)
    val result = SampleFeatures.results[feature]

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                feature.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (status is SampleFeatureStatus.Available) colors.ink else colors.inkFaint,
            )
            when (status) {
                is SampleFeatureStatus.Unavailable -> Text(
                    status.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.inkFaint,
                )

                is SampleFeatureStatus.Available -> {
                    Text(feature.description, style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
                    when (result) {
                        null -> Unit
                        ActionResult.Running -> Text("Running…", style = MaterialTheme.typography.bodySmall, color = colors.inkMuted)
                        is ActionResult.Success -> Text(
                            text = "✓ " + listOfNotNull(result.detail, "${result.millis} ms").joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.success,
                        )

                        is ActionResult.Failure -> Text(
                            text = "✕ ${result.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.error,
                        )
                    }
                }
            }
            if (feature.platforms != SamplePlatform.All) {
                PlatformChips(
                    supported = feature.platforms,
                    current = SampleFeatures.platform,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        AccentButton(
            text = feature.buttonLabel,
            enabled = status is SampleFeatureStatus.Available,
            onClick = { scope.launch { SampleFeatures.run(feature) } },
        )
    }
}

/** A small tinted note: warnings, tips, how to trigger something from the outside. */
@Composable
fun InfoCard(text: String, tint: Color = SampleTheme.colors.warning) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.12f))
            .border(1.dp, tint.copy(alpha = 0.33f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = SampleTheme.colors.ink)
    }
}

@Composable
fun Pill(text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SampleTheme.colors.surfaceHigh)
            .border(1.dp, SampleTheme.colors.line, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

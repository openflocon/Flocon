package io.github.openflocon.flocon.myapplication.multi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.SamplePlatform
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

/**
 * One chip per platform: green with a check when it is in [supported], struck through and faded
 * otherwise. The chip of the [current] platform has an accent outline.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlatformChips(
    supported: Set<SamplePlatform>,
    current: SamplePlatform?,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SamplePlatform.entries.forEach { platform ->
            PlatformChip(
                platform = platform,
                isSupported = platform in supported,
                isCurrent = platform == current,
            )
        }
    }
}

@Composable
private fun PlatformChip(platform: SamplePlatform, isSupported: Boolean, isCurrent: Boolean) {
    val colors = SampleTheme.colors
    val tint = if (isSupported) colors.success else colors.inkFaint
    val borderColor = when {
        isCurrent -> colors.accent
        isSupported -> colors.success.copy(alpha = 0.33f)
        else -> colors.line
    }
    val description = buildString {
        append(platform.displayName)
        append(if (isSupported) ": available" else ": not available")
        if (isCurrent) append(", this platform")
    }

    Text(
        text = (if (isSupported) "✓ " else "✕ ") + platform.label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
        color = tint,
        textDecoration = if (isSupported) TextDecoration.None else TextDecoration.LineThrough,
        modifier = Modifier
            .clearAndSetSemantics { contentDescription = description }
            .clip(CircleShape)
            .background(if (isSupported) colors.success.copy(alpha = 0.12f) else Color.Transparent)
            .border(if (isCurrent) 1.5.dp else 1.dp, borderColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

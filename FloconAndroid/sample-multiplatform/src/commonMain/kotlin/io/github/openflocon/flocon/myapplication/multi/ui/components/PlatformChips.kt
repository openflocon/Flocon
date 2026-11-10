package io.github.openflocon.flocon.myapplication.multi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.ui.SamplePlatform
import io.github.openflocon.flocon.myapplication.multi.ui.theme.SampleTheme

/**
 * For list items: one chip per platform in a single horizontal row (scrollable when it doesn't fit).
 * Green with a check when it is in [supported], struck through and faded otherwise. The chip of
 * the [current] platform has an accent outline.
 */
@Composable
fun PlatformChips(
    supported: Set<SamplePlatform>,
    current: SamplePlatform?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SamplePlatform.entries.forEach { platform ->
            PlatformChip(
                text = platform.label,
                isSupported = platform in supported,
                isCurrent = platform == current,
                description = platform.description(platform in supported, platform == current),
            )
        }
    }
}

/**
 * For small items: whether the [current] platform is supported, and a chip with the number of
 * supported platforms. Clicking it opens a bottom sheet with the whole list.
 */
@Composable
fun PlatformSummary(
    supported: Set<SamplePlatform>,
    current: SamplePlatform?,
    modifier: Modifier = Modifier,
) {
    var showSheet by remember { mutableStateOf(false) }
    val total = SamplePlatform.entries.size
    val count = SamplePlatform.entries.count { it in supported }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (current != null) {
            PlatformChip(
                text = current.label,
                isSupported = current in supported,
                isCurrent = true,
                description = current.description(current in supported, true),
            )
        }
        PlatformChip(
            text = "$count/$total platforms",
            isSupported = false,
            isCurrent = false,
            description = "$count of $total platforms, show the list",
            neutral = true,
            modifier = Modifier.clickable(role = Role.Button) { showSheet = true },
        )
    }

    if (showSheet) {
        PlatformSheet(supported = supported, current = current, onDismiss = { showSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlatformSheet(
    supported: Set<SamplePlatform>,
    current: SamplePlatform?,
    onDismiss: () -> Unit,
) {
    val colors = SampleTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Platforms",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.ink,
            )
            SamplePlatform.entries.forEach { platform ->
                val isSupported = platform in supported
                val isCurrent = platform == current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics { contentDescription = platform.description(isSupported, isCurrent) },
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlatformChip(
                        text = if (isSupported) "Available" else "Not available",
                        isSupported = isSupported,
                        isCurrent = isCurrent,
                        description = null,
                    )
                    Text(
                        text = platform.displayName + if (isCurrent) " (this platform)" else "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSupported) colors.ink else colors.inkFaint,
                    )
                }
            }
        }
    }
}

private fun SamplePlatform.description(isSupported: Boolean, isCurrent: Boolean): String = buildString {
    append(displayName)
    append(if (isSupported) ": available" else ": not available")
    if (isCurrent) append(", this platform")
}

@Composable
private fun PlatformChip(
    text: String,
    isSupported: Boolean,
    isCurrent: Boolean,
    description: String?,
    modifier: Modifier = Modifier,
    neutral: Boolean = false,
) {
    val colors = SampleTheme.colors
    val tint = when {
        neutral -> colors.inkMuted
        isSupported -> colors.success
        else -> colors.inkFaint
    }
    val borderColor = when {
        isCurrent -> colors.accent
        neutral -> colors.line
        isSupported -> colors.success.copy(alpha = 0.33f)
        else -> colors.line
    }
    val background = when {
        neutral -> colors.surfaceHigh
        isSupported -> colors.success.copy(alpha = 0.12f)
        else -> Color.Transparent
    }
    val prefix = when {
        neutral -> ""
        isSupported -> "✓ "
        else -> "✕ "
    }

    Text(
        text = prefix + text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
        color = tint,
        textDecoration = if (isSupported || neutral) TextDecoration.None else TextDecoration.LineThrough,
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .let { if (description != null) it.clearAndSetSemantics { contentDescription = description } else it }
            .clip(CircleShape)
            .background(background)
            .border(if (isCurrent) 1.5.dp else 1.dp, borderColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

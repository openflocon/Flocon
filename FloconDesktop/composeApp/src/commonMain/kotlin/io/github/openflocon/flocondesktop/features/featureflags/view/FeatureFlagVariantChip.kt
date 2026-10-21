package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private data class FeatureFlagStateColors(
    val container: Color,
    val content: Color,
    val indicator: Color,
)

// the palette has no blue: the custom state gets its own, per theme
@Composable
private fun FeatureFlagStateUiModel.colors(): FeatureFlagStateColors {
    val colors = FloconTheme.colorPalette
    return when (this) {
        FeatureFlagStateUiModel.On -> FeatureFlagStateColors(
            container = colors.accent,
            content = colors.onAccent,
            indicator = if (FloconTheme.isDarkTheme) colors.onAccent else colors.accent,
        )
        FeatureFlagStateUiModel.Off -> FeatureFlagStateColors(
            container = Color.Transparent,
            content = colors.onSecondary,
            indicator = colors.onSecondary,
        )
        FeatureFlagStateUiModel.Custom -> if (FloconTheme.isDarkTheme) {
            FeatureFlagStateColors(container = Color(0xFF23304D), content = Color(0xFFA9C1FF), indicator = Color(0xFF7FA7FF))
        } else {
            FeatureFlagStateColors(container = Color(0xFFDCE6FF), content = Color(0xFF1F3F8F), indicator = Color(0xFF3B6FD8))
        }
    }
}

/**
 * The variant of a flag, colored by its state: on (accent), off (outlined), custom (blue)
 */
@Composable
internal fun FeatureFlagVariantChip(
    state: FeatureFlagStateUiModel,
    variant: String,
    modifier: Modifier = Modifier,
) {
    val colors = state.colors()
    Text(
        text = variant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = FloconTheme.typography.bodySmall,
        color = colors.content,
        modifier = modifier
            .background(color = colors.container, shape = FloconTheme.shapes.extraLarge)
            .then(
                if (state == FeatureFlagStateUiModel.Off) {
                    Modifier.border(width = 1.dp, color = FloconTheme.colorPalette.secondary, shape = FloconTheme.shapes.extraLarge)
                } else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 1.dp),
    )
}

/**
 * On: a filled dot, off: an outlined dot, custom: a small square
 */
@Composable
internal fun FeatureFlagStateDot(
    state: FeatureFlagStateUiModel,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    val color = state.colors().indicator
    Box(
        modifier = modifier
            .size(size)
            .then(
                when (state) {
                    FeatureFlagStateUiModel.On -> Modifier.background(color = color, shape = CircleShape)
                    FeatureFlagStateUiModel.Off -> Modifier.border(width = 1.5.dp, color = color, shape = CircleShape)
                    FeatureFlagStateUiModel.Custom -> Modifier.background(color = color, shape = RoundedCornerShape(2.dp))
                }
            ),
    )
}

@Preview
@Composable
private fun FeatureFlagVariantChipPreview() {
    FloconTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureFlagStateUiModel.entries.forEach {
                FeatureFlagStateDot(state = it)
                FeatureFlagVariantChip(state = it, variant = it.name)
            }
        }
    }
}

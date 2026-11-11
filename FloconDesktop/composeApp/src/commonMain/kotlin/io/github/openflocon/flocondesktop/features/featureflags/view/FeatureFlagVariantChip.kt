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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.FloconColors

internal data class FeatureFlagStateColors(
    val container: Color,
    val content: Color,
    val indicator: Color,
)

/**
 * on (accent), off (muted), a custom variant gets a stable color of its own, picked from its name,
 * so the same variant has the same color in the filters, the list and the detail
 */
@Composable
internal fun FeatureFlagStateUiModel.colors(variant: String): FeatureFlagStateColors {
    val colors = FloconTheme.colors
    return when (this) {
        FeatureFlagStateUiModel.On -> FeatureFlagStateColors(
            container = colors.accentSoft,
            content = colors.accent,
            indicator = colors.accent,
        )

        FeatureFlagStateUiModel.Off -> FeatureFlagStateColors(
            container = Color.Transparent,
            content = colors.inkMuted,
            indicator = colors.inkMuted,
        )

        FeatureFlagStateUiModel.Custom -> {
            val (color, soft) = customVariantPalette(colors)[variant.lowercase().hashCode().mod(CustomVariantColorCount)]
            FeatureFlagStateColors(container = soft, content = color, indicator = color)
        }
    }
}

private const val CustomVariantColorCount = 7

private fun customVariantPalette(colors: FloconColors) = listOf(
    colors.catNetwork to colors.catNetworkSoft,
    colors.catDatabase to colors.catDatabaseSoft,
    colors.catAnalyticsEvent to colors.catAnalyticsEventSoft,
    colors.catTable to colors.catTableSoft,
    colors.catDeeplink to colors.catDeeplinkSoft,
    colors.catAdb to colors.catAdbSoft,
    colors.catWebView to colors.catWebViewSoft,
)

/**
 * The variant of a flag, colored by its state: on (accent), off (outlined), custom (one color per variant)
 */
@Composable
internal fun FeatureFlagVariantChip(
    state: FeatureFlagStateUiModel,
    variant: String,
    modifier: Modifier = Modifier,
) {
    val colors = state.colors(variant)
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
                    Modifier.border(width = 1.dp, color = FloconTheme.colors.bg200, shape = FloconTheme.shapes.extraLarge)
                } else {
                    Modifier
                }
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
    variant: String,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    val color = state.colors(variant).indicator
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
        Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
            listOf(
                FeatureFlagStateUiModel.On to "on",
                FeatureFlagStateUiModel.Off to "off",
                FeatureFlagStateUiModel.Custom to "control",
                FeatureFlagStateUiModel.Custom to "treatment_a",
                FeatureFlagStateUiModel.Custom to "treatment_b",
            ).forEach { (state, variant) ->
                FeatureFlagStateDot(state = state, variant = variant)
                FeatureFlagVariantChip(state = state, variant = variant)
            }
        }
    }
}

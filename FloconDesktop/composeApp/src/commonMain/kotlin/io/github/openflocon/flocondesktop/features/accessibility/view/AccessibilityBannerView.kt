package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_banner_not_enabled
import flocondesktop.composeapp.generated.resources.accessibility_banner_not_supported
import flocondesktop.composeapp.generated.resources.accessibility_banner_services
import flocondesktop.composeapp.generated.resources.accessibility_touch_exploration
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityBannerUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Explains why the events may be missing, or which services are running
 */
@Composable
internal fun AccessibilityBannerView(
    banner: AccessibilityBannerUiModel,
    modifier: Modifier = Modifier,
) {
    val text = when (banner) {
        AccessibilityBannerUiModel.NotSupported -> stringResource(Res.string.accessibility_banner_not_supported)
        AccessibilityBannerUiModel.NotEnabled -> stringResource(Res.string.accessibility_banner_not_enabled)
        is AccessibilityBannerUiModel.Running -> {
            val running = stringResource(
                Res.string.accessibility_banner_services,
                banner.services.joinToString(", ").ifEmpty { "-" },
            )
            if (banner.touchExploration) {
                running + " (" + stringResource(Res.string.accessibility_touch_exploration) + ")"
            } else {
                running
            }
        }
    }

    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.ink,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg100)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Preview
@Composable
private fun AccessibilityBannerViewPreview() {
    FloconTheme {
        AccessibilityBannerView(
            banner = AccessibilityBannerUiModel.NotEnabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

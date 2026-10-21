package io.github.openflocon.flocondesktop.common.ui.featuredoc

import flocondesktop.composeapp.generated.resources.feature_doc_getting_started
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_doc_how_to_setup
import flocondesktop.composeapp.generated.resources.feature_doc_no_device_description
import flocondesktop.composeapp.generated.resources.feature_doc_no_device_title
import flocondesktop.composeapp.generated.resources.feature_doc_open_documentation
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration.FeatureIllustration
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconEmptyState
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Small (i) button, always visible on a page, opening its documentation.
 */
@Composable
fun FeatureDocButton(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    FloconIconButton(
        imageVector = Icons.Outlined.Info,
        tooltip = stringResource(Res.string.feature_doc_open_documentation),
        onClick = { runCatching { uriHandler.openUri(doc.url) } },
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
    )
}

/**
 * Placeholder displayed when a page has no content: illustration of the feature,
 * a short explanation and a "How to set up" button opening the setup section of its documentation.
 * When the selected device is not connected (see [LocalHasDevice]), a notice below says so.
 */
@Composable
fun FeatureEmptyState(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
    illustrationSize: Int = 160,
) {
    val uriHandler = LocalUriHandler.current
    val hasDevice = LocalHasDevice.current
    FloconEmptyState(
        title = doc.emptyTitle?.let { stringResource(it) }.orEmpty(),
        description = doc.emptyDescription?.let { stringResource(it) }.orEmpty(),
        modifier = modifier,
        illustration = {
            FeatureIllustration(
                doc = doc,
                modifier = Modifier.size(width = illustrationSize.dp, height = (illustrationSize * 0.75f).dp),
            )
        },
        action = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraLarge),
            ) {
                FloconButton(
                    onClick = { runCatching { uriHandler.openUri(doc.setupUrl) } },
                    containerColor = FloconTheme.colors.accent,
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.Description,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(text = stringResource(Res.string.feature_doc_how_to_setup))
                    }
                }
                if (!hasDevice) {
                    NoDeviceNoticeView(
                        onGettingStartedClicked = { runCatching { uriHandler.openUri(FeatureDoc.SETUP_URL) } },
                    )
                }
            }
        },
    )
}

/**
 * "No device connected" card, with a slowly pulsing dot: Flocon is waiting for an app.
 */
@Composable
private fun NoDeviceNoticeView(
    onGettingStartedClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1_200), RepeatMode.Reverse),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(colors.bg200)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(16.dp)) {
            Box(
                Modifier
                    .size(16.dp)
                    .background(colors.statusWarning.copy(alpha = pulse), CircleShape)
            )
            Box(
                Modifier
                    .size(8.dp)
                    .background(colors.statusWarning, CircleShape)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
            modifier = Modifier.weight(1f, fill = false),
        ) {
            Text(
                text = stringResource(Res.string.feature_doc_no_device_title),
                style = FloconTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.ink,
            )
            Text(
                text = stringResource(Res.string.feature_doc_no_device_description),
                style = FloconTheme.typography.bodySmall,
                color = colors.inkMuted,
            )
        }
        Text(
            text = stringResource(Res.string.feature_doc_getting_started),
            style = FloconTheme.typography.labelLarge,
            color = colors.accent,
            modifier = Modifier
                .clip(FloconTheme.shapes.small)
                .clickable(onClick = onGettingStartedClicked)
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
        )
    }
}

@Preview
@Composable
private fun FeatureEmptyStatePreview() {
    FloconTheme {
        FeatureEmptyState(doc = FeatureDoc.Network)
    }
}

@Preview
@Composable
private fun FeatureEmptyStateNoDevicePreview() {
    FloconTheme {
        CompositionLocalProvider(LocalHasDevice provides false) {
            FeatureEmptyState(doc = FeatureDoc.Network)
        }
    }
}

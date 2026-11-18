package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_fit
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_in
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_out
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_reset
import io.github.openflocon.flocondesktop.features.images.mapper.formatImageZoom
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource

private val ZoomLabelWidth = 56.dp

@Composable
internal fun ImageViewerZoomControls(
    zoomState: ImageZoomState,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.bg200)
            .padding(FloconTheme.spacing.tiny),
    ) {
        FloconIconButton(
            imageVector = Icons.Default.Remove,
            tooltip = stringResource(Res.string.images_viewer_zoom_out),
            onClick = { zoomState.zoomBy(1f / ImageZoomStep) },
        )
        Text(
            text = formatImageZoom(zoomState.scale) ?: stringResource(Res.string.images_viewer_zoom_fit),
            style = FloconTheme.typography.labelMedium,
            color = FloconTheme.colors.ink,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(ZoomLabelWidth)
                .clip(FloconTheme.shapes.small)
                .clickable(
                    onClickLabel = stringResource(Res.string.images_viewer_zoom_reset),
                    role = Role.Button,
                    onClick = zoomState::reset,
                )
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(vertical = FloconTheme.spacing.extraSmall),
        )
        FloconIconButton(
            imageVector = Icons.Default.Add,
            tooltip = stringResource(Res.string.images_viewer_zoom_in),
            onClick = { zoomState.zoomBy(ImageZoomStep) },
        )
    }
}

package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_viewer_close
import flocondesktop.composeapp.generated.resources.images_viewer_copy_url
import flocondesktop.composeapp.generated.resources.images_viewer_download
import flocondesktop.composeapp.generated.resources.images_viewer_open_in_browser
import io.github.openflocon.flocondesktop.common.ui.window.LocalWindowTitleBar
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.view.ImageFormatBadge
import io.github.openflocon.flocondesktop.features.images.view.ImageMonoText
import io.github.openflocon.flocondesktop.features.images.view.ImageTimeText
import io.github.openflocon.flocondesktop.features.images.view.ImageUrlText
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImageViewerTopBar(
    model: ImagesUiModel,
    dimensions: String?,
    zoomState: ImageZoomState,
    onCopyUrl: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onDownload: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = FloconTheme.colors.line
    // the viewer covers the whole window: keep the bar below the window buttons
    val titleBarInset = LocalWindowTitleBar.current.topInset

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .background(FloconTheme.colors.floating)
            .drawBottomLine(lineColor)
            .padding(
                start = FloconTheme.spacing.large,
                top = FloconTheme.spacing.medium + titleBarInset,
                end = FloconTheme.spacing.large,
                bottom = FloconTheme.spacing.medium,
            ),
    ) {
        model.format?.let { ImageFormatBadge(format = it) }
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
            modifier = Modifier.weight(1f),
        ) {
            ImageUrlText(host = model.host, path = model.path)
            Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium)) {
                dimensions?.let { ImageMonoText(text = it, color = FloconTheme.colors.inkMuted) }
                model.weight?.let { ImageMonoText(text = it, color = FloconTheme.colors.inkMuted) }
                ImageTimeText(model = model, main = FloconTheme.colors.inkFaint)
            }
        }
        ImageViewerZoomControls(zoomState = zoomState)
        Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)) {
            FloconIconButton(
                imageVector = Icons.Outlined.ContentCopy,
                tooltip = stringResource(Res.string.images_viewer_copy_url),
                onClick = onCopyUrl,
            )
            FloconIconButton(
                imageVector = Icons.Outlined.OpenInBrowser,
                tooltip = stringResource(Res.string.images_viewer_open_in_browser),
                onClick = onOpenInBrowser,
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Download,
                tooltip = stringResource(Res.string.images_viewer_download),
                onClick = onDownload,
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Close,
                tooltip = stringResource(Res.string.images_viewer_close),
                onClick = onClose,
            )
        }
    }
}

@Preview
@Composable
private fun ImageViewerTopBarPreview() {
    FloconTheme {
        ImageViewerTopBar(
            model = previewImageViewerUiModel().current,
            dimensions = "1000×1200",
            zoomState = ImageZoomState(),
            onCopyUrl = {},
            onOpenInBrowser = {},
            onDownload = {},
            onClose = {},
        )
    }
}

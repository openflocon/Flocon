package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_viewer_next
import flocondesktop.composeapp.generated.resources.images_viewer_previous
import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.view.rememberImageDimensions
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ImageViewerContent(
    model: ImageViewerUiModel,
    onImageSelected: (id: String) -> Unit,
    onCopyUrl: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onDownload: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // back to fit whenever another image is displayed
    val zoomState = remember(model.current.id) { ImageZoomState() }
    val focusRequester = remember { FocusRequester() }
    val dimensions by rememberImageDimensions(model.current)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionLeft -> model.previousId?.let(onImageSelected).let { true }
                    Key.DirectionRight -> model.nextId?.let(onImageSelected).let { true }
                    Key.Escape -> onClose().let { true }
                    Key.Plus, Key.Equals, Key.NumPadAdd -> zoomState.zoomBy(ImageZoomStep).let { true }
                    Key.Minus, Key.NumPadSubtract -> zoomState.zoomBy(1f / ImageZoomStep).let { true }
                    Key.Zero, Key.NumPad0 -> zoomState.reset().let { true }
                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable(),
    ) {
        ImageViewerTopBar(
            model = model.current,
            dimensions = dimensions,
            zoomState = zoomState,
            onCopyUrl = onCopyUrl,
            onOpenInBrowser = onOpenInBrowser,
            onDownload = onDownload,
            onClose = onClose,
            modifier = Modifier.fillMaxWidth(),
        )
        FloconHorizontalDivider()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black),
        ) {
            ImageViewerCanvas(
                model = model.current,
                zoomState = zoomState,
                modifier = Modifier.fillMaxSize(),
            )
            model.previousId?.let { id ->
                ImageViewerNavigationButton(
                    icon = Icons.Default.ChevronLeft,
                    contentDescription = stringResource(Res.string.images_viewer_previous),
                    onClick = { onImageSelected(id) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(FloconTheme.spacing.large),
                )
            }
            model.nextId?.let { id ->
                ImageViewerNavigationButton(
                    icon = Icons.Default.ChevronRight,
                    contentDescription = stringResource(Res.string.images_viewer_next),
                    onClick = { onImageSelected(id) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(FloconTheme.spacing.large),
                )
            }
        }
        FloconHorizontalDivider()
        ImageViewerFilmstrip(
            images = model.images,
            currentIndex = model.currentIndex,
            positionText = model.positionText,
            onImageSelected = onImageSelected,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ImageViewerContentPreview() {
    FloconTheme {
        ImageViewerContent(
            model = previewImageViewerUiModel(),
            onImageSelected = {},
            onCopyUrl = {},
            onOpenInBrowser = {},
            onDownload = {},
            onClose = {},
            modifier = Modifier
                .width(900.dp)
                .height(560.dp),
        )
    }
}

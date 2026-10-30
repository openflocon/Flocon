package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_thumbnail
import flocondesktop.composeapp.generated.resources.images_viewer_close
import flocondesktop.composeapp.generated.resources.images_viewer_copy_url
import flocondesktop.composeapp.generated.resources.images_viewer_next
import flocondesktop.composeapp.generated.resources.images_viewer_open_in_browser
import flocondesktop.composeapp.generated.resources.images_viewer_previous
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_fit
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_in
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_out
import flocondesktop.composeapp.generated.resources.images_viewer_zoom_reset
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.common.ui.window.LocalWindowTitleBar
import io.github.openflocon.flocondesktop.features.images.mapper.formatImageZoom
import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImageViewerUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import kotlin.math.pow

private val NavigationButtonSize = 48.dp
private val NavigationButtonAlpha = 0.7f
private val ThumbnailWidth = 52.dp
private val ThumbnailHeight = 62.dp
private val ThumbnailDimmedAlpha = 0.55f
private val SelectedBorderWidth = 2.dp
private val LineWidth = 1.dp
private val ZoomLabelWidth = 56.dp
private const val ZoomPerScrollUnit = 1.1f

@Composable
fun ImageViewer(
    model: ImageViewerUiModel,
    onImageSelected: (id: String) -> Unit,
    onCopyUrl: (url: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        ImageViewerContent(
            model = model,
            onImageSelected = onImageSelected,
            onCopyUrl = { onCopyUrl(model.current.url) },
            onOpenInBrowser = { uriHandler.openUri(model.current.url) },
            onClose = onClose,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ImageViewerContent(
    model: ImageViewerUiModel,
    onImageSelected: (id: String) -> Unit,
    onCopyUrl: () -> Unit,
    onOpenInBrowser: () -> Unit,
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
            onClose = onClose,
            modifier = Modifier.fillMaxWidth(),
        )
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
        ImageViewerFilmstrip(
            images = model.images,
            currentIndex = model.currentIndex,
            positionText = model.positionText,
            onImageSelected = onImageSelected,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ImageViewerCanvas(
    model: ImagesUiModel,
    zoomState: ImageZoomState,
    modifier: Modifier = Modifier,
) {
    // last known pointer position, relative to the canvas center: the pinch zooms around it
    val pointer = remember { mutableStateOf(Offset.Zero) }
    PinchZoomEffect { factor -> zoomState.zoomBy(factor, pointer.value) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { zoomState.viewportSize = it }
            .pointerInput(zoomState) {
                // two-finger scroll / wheel pans (zoomed only), ctrl or cmd + wheel zooms around the pointer
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        when (event.type) {
                            PointerEventType.Move, PointerEventType.Enter -> pointer.value = change.position.fromCenter(size)

                            PointerEventType.Scroll -> {
                                if (event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed) {
                                    val factor = ZoomPerScrollUnit.pow(-change.scrollDelta.y)
                                    zoomState.zoomBy(factor, change.position.fromCenter(size))
                                } else {
                                    zoomState.panByScroll(change.scrollDelta)
                                }
                                change.consume()
                            }
                        }
                    }
                }
            }
            .pointerInput(zoomState) {
                detectTapGestures(onDoubleTap = { zoomState.toggleZoom(it.fromCenter(size)) })
            }
            .pointerInput(zoomState) {
                detectDragGestures { change, dragAmount ->
                    if (zoomState.isZoomed) {
                        change.consume()
                        zoomState.panBy(dragAmount)
                    }
                }
            }
            .pointerHoverIcon(if (zoomState.isZoomed) PointerIcon.Hand else PointerIcon.Default),
    ) {
        if (isInPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FloconTheme.colors.bg300),
            )
        } else {
            AsyncImage(
                model = rememberImageRequest(model),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomState.scale
                        scaleY = zoomState.scale
                        translationX = zoomState.offset.x
                        translationY = zoomState.offset.y
                    },
            )
        }
    }
}

private fun Offset.fromCenter(size: IntSize) = this - Offset(size.width / 2f, size.height / 2f)

@Composable
private fun ImageViewerTopBar(
    model: ImagesUiModel,
    dimensions: String?,
    zoomState: ImageZoomState,
    onCopyUrl: () -> Unit,
    onOpenInBrowser: () -> Unit,
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
                imageVector = Icons.Outlined.Close,
                tooltip = stringResource(Res.string.images_viewer_close),
                onClick = onClose,
            )
        }
    }
}

@Composable
private fun ImageViewerZoomControls(
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

@Composable
private fun ImageViewerNavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(NavigationButtonSize)
            .clip(CircleShape)
            .background(FloconTheme.colors.floating.copy(alpha = NavigationButtonAlpha))
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = FloconTheme.colors.ink,
        )
    }
}

@Composable
private fun ImageViewerFilmstrip(
    images: ImmutableList<ImagesUiModel>,
    currentIndex: Int,
    positionText: String,
    onImageSelected: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val lineColor = FloconTheme.colors.line

    LaunchedEffect(currentIndex) {
        listState.animateScrollToItem(currentIndex)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .background(FloconTheme.colors.floating)
            .drawTopLine(lineColor)
            .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.medium),
    ) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            contentPadding = PaddingValues(all = SelectedBorderWidth),
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(items = images, key = { _, item -> item.id }) { index, item ->
                ImageViewerThumbnail(
                    model = item,
                    selected = index == currentIndex,
                    onClick = { onImageSelected(item.id) },
                )
            }
        }
        Text(
            text = positionText,
            style = FloconTheme.typography.monoSmall,
            color = FloconTheme.colors.inkFaint,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun ImageViewerThumbnail(
    model: ImagesUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FloconTheme.shapes.small
    Box(
        modifier = modifier
            .size(width = ThumbnailWidth, height = ThumbnailHeight)
            .alpha(if (selected) 1f else ThumbnailDimmedAlpha)
            .clip(shape)
            .background(Color.Black)
            .border(
                width = SelectedBorderWidth,
                color = if (selected) FloconTheme.colors.accent else Color.Transparent,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        if (isInPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FloconTheme.colors.bg300),
            )
        } else {
            AsyncImage(
                model = rememberImageRequest(model),
                contentDescription = stringResource(Res.string.images_thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun Modifier.drawBottomLine(color: Color): Modifier = drawLine(color = color, top = false)

private fun Modifier.drawTopLine(color: Color): Modifier = drawLine(color = color, top = true)

private fun Modifier.drawLine(color: Color, top: Boolean): Modifier = this.drawWithContent {
    drawContent()
    val thickness = LineWidth.toPx()
    drawRect(
        color = color,
        topLeft = Offset(0f, if (top) 0f else size.height - thickness),
        size = Size(size.width, thickness),
    )
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
            onClose = {},
        )
    }
}

@Preview
@Composable
private fun ImageViewerFilmstripPreview() {
    FloconTheme {
        val model = previewImageViewerUiModel()
        ImageViewerFilmstrip(
            images = model.images,
            currentIndex = model.currentIndex,
            positionText = model.positionText,
            onImageSelected = {},
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
            onClose = {},
            modifier = Modifier
                .width(900.dp)
                .height(560.dp),
        )
    }
}

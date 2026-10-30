package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_thumbnail
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.view.rememberImageRequest
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

private val ThumbnailWidth = 52.dp
private val ThumbnailHeight = 62.dp
private val ThumbnailDimmedAlpha = 0.55f
internal val SelectedBorderWidth = 2.dp

@Composable
internal fun ImageViewerThumbnail(
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

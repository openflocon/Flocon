package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_column_format
import flocondesktop.composeapp.generated.resources.images_label_size
import flocondesktop.composeapp.generated.resources.images_label_time
import flocondesktop.composeapp.generated.resources.images_label_url
import flocondesktop.composeapp.generated.resources.images_label_weight
import flocondesktop.composeapp.generated.resources.images_thumbnail
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import org.jetbrains.compose.resources.stringResource

internal val TimeWidth = 104.dp
internal val ThumbnailWidth = 64.dp
private val ThumbnailHeight = 43.dp
internal val DimensionsWidth = 88.dp
internal val FormatWidth = 48.dp
internal val WeightWidth = 72.dp

@Composable
fun ImageListItem(
    model: ImagesUiModel,
    onClick: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions by rememberImageDimensions(model)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(model) })
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
    ) {
        ImageTimeText(
            model = model,
            modifier = Modifier.width(TimeWidth),
        )
        Box(
            modifier = Modifier
                .size(width = ThumbnailWidth, height = ThumbnailHeight)
                .clip(FloconTheme.shapes.small)
                .background(FloconTheme.colors.bg000),
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
        ImageUrlText(
            host = model.host,
            path = model.path,
            modifier = Modifier.weight(1f),
        )
        ImageMonoText(
            text = dimensions.orEmpty(),
            color = FloconTheme.colors.inkMuted,
            modifier = Modifier.width(DimensionsWidth),
        )
        ImageMonoText(
            text = model.format.orEmpty(),
            color = FloconTheme.colors.inkFaint,
            modifier = Modifier.width(FormatWidth),
        )
        ImageMonoText(
            text = model.weight.orEmpty(),
            color = FloconTheme.colors.inkMuted,
            align = TextAlign.End,
            modifier = Modifier.width(WeightWidth),
        )
    }
}

@Composable
internal fun ImageListHeader(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        modifier = modifier
            .fillMaxWidth()
            .floconTableHeader()
            .padding(horizontal = FloconTheme.spacing.medium),
    ) {
        FloconTableHeaderLabel(
            text = stringResource(Res.string.images_label_time),
            modifier = Modifier.width(TimeWidth),
        )
        Spacer(Modifier.width(ThumbnailWidth))
        FloconTableHeaderLabel(
            text = stringResource(Res.string.images_label_url),
            modifier = Modifier.weight(1f),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.images_label_size),
            modifier = Modifier.width(DimensionsWidth),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.images_column_format),
            modifier = Modifier.width(FormatWidth),
        )
        FloconTableHeaderLabel(
            text = stringResource(Res.string.images_label_weight),
            textAlign = TextAlign.End,
            modifier = Modifier.width(WeightWidth),
        )
    }
}

@Preview
@Composable
private fun ImageListItemPreview() {
    FloconTheme {
        ImageListItem(
            model = previewImagesUiModel(),
            onClick = {},
            modifier = Modifier.height(64.dp),
        )
    }
}

@Preview
@Composable
private fun ImageListItemLongUrlNoWeightPreview() {
    FloconTheme {
        ImageListItem(
            model = previewImagesUiModel(
                url = "https://very-long-subdomain.images.example.com/assets/products/2024/summer/hero-banner-large.jpg?width=1200",
                weight = null,
                format = null,
            ),
            onClick = {},
            modifier = Modifier.width(600.dp),
        )
    }
}

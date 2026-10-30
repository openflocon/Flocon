package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.images_label_size
import flocondesktop.composeapp.generated.resources.images_label_time
import flocondesktop.composeapp.generated.resources.images_label_url
import flocondesktop.composeapp.generated.resources.images_label_weight
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

private val LabelWidth = 56.dp
private val BadgeAlpha = 0.7f

@Composable
fun ImageItem(
    model: ImagesUiModel,
    onClick: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions by rememberImageDimensions(model)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val shape = FloconTheme.shapes.medium

    Column(
        modifier = modifier
            .clip(shape)
            .background(FloconTheme.colors.bg100)
            .border(
                width = 1.dp,
                color = if (isHovered) FloconTheme.colors.accent else FloconTheme.colors.line,
                shape = shape,
            )
            .hoverable(interactionSource)
            .clickable(onClick = { onClick(model) })
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Color.Black),
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
                    modifier = Modifier.fillMaxSize(),
                )
            }
            model.format?.let {
                ImageFormatBadge(
                    format = it,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(FloconTheme.spacing.small),
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        ) {
            ImageInfoRow(label = stringResource(Res.string.images_label_url)) {
                ImageUrlText(host = model.host, path = model.path)
            }
            dimensions?.let {
                ImageInfoRow(label = stringResource(Res.string.images_label_size)) {
                    ImageMonoText(text = it)
                }
            }
            model.weight?.let {
                ImageInfoRow(label = stringResource(Res.string.images_label_weight)) {
                    ImageMonoText(text = it)
                }
            }
            ImageInfoRow(label = stringResource(Res.string.images_label_time)) {
                ImageTimeText(model = model)
            }
        }
    }
}

@Composable
private fun ImageInfoRow(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.labelSmall,
            color = FloconTheme.colors.inkFaint,
            modifier = Modifier.width(LabelWidth),
        )
        Box(modifier = Modifier.weight(1f)) {
            value()
        }
    }
}

@Composable
internal fun ImageMonoText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colors.ink,
    align: TextAlign? = null,
) {
    Text(
        text = text,
        style = FloconTheme.typography.monoSmall,
        color = color,
        textAlign = align,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
internal fun ImageFormatBadge(
    format: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = format,
        style = FloconTheme.typography.monoSmall,
        color = FloconTheme.colors.ink,
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(FloconTheme.colors.floating.copy(alpha = BadgeAlpha))
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
    )
}

@Preview
@Composable
private fun ImageItemPreview() {
    FloconTheme {
        ImageItem(
            modifier = Modifier.width(260.dp),
            model = previewImagesUiModel(),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun ImageItemLongUrlNoWeightPreview() {
    FloconTheme {
        ImageItem(
            modifier = Modifier.width(260.dp),
            model = previewImagesUiModel(
                url = "https://very-long-subdomain.images.example.com/assets/products/2024/summer/hero-banner-large.jpg?width=1200",
                weight = null,
                format = null,
            ),
            onClick = {},
        )
    }
}

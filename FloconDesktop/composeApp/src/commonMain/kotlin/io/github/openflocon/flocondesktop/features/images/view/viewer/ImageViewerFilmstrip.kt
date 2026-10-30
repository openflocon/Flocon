package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.style.TextOverflow
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImageViewerUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ImageViewerFilmstrip(
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

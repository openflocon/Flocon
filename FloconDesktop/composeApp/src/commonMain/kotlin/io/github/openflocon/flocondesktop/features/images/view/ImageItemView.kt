package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageItemView(
    model: ImagesUiModel,
    onClick: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(1.dp, FloconTheme.colorPalette.secondary, FloconTheme.shapes.medium)
            .clickable(onClick = { onClick(model) })
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Color.Black)
        ) {
            if (isInPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                )
            } else {
                AsyncImage(
                    model = model.url,
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = model.downloadedAt,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary,
            )
            Text(
                text = model.url,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = .5f),
            )
        }
    }
}

@Preview
@Composable
private fun ImageItemViewPreview() {
    FloconTheme {
        ImageItemView(
            modifier = Modifier.size(200.dp),
            model = previewImagesUiModel(),
            onClick = {},
        )
    }
}

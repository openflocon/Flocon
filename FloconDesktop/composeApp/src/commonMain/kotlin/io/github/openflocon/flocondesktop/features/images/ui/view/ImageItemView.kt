package io.github.openflocon.flocondesktop.features.images.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.ui.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.ui.model.previewImagesUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageItemView(
    model: ImagesUiModel,
    onClick: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FloconColors.pannel)
            .clickable(onClick = {
                onClick(model)
            }),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
        ) {
            if (isInPreview) {
                Box(
                    modifier = Modifier.fillMaxSize()
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
                .padding(all = 12.dp),
        ) {
            Text(text = model.downloadedAt, color = Color.White, fontSize = 14.sp)
            Text(text = model.url, color = Color.Gray, fontSize = 10.sp)
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

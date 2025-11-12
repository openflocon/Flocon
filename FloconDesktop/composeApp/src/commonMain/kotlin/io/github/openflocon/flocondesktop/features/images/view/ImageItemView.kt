package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.size.SizeResolver
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.model.previewImagesUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ImageItemView(
    model: ImagesUiModel,
    onClick: (ImagesUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var size by remember(model) { mutableStateOf<Size?>(null) }
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
                    model = remember(model) {
                        ImageRequest.Builder(PlatformContext.INSTANCE)
                            .data(model.url)
                            .httpHeaders(
                                NetworkHeaders.Builder().apply {
                                    model.headers?.forEach { (key, value) ->
                                        set(key, value)
                                    }
                                }.build()
                            )
                            .build()
                    },
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )

                val platformContext = LocalPlatformContext.current
                LaunchedEffect(key1 = model.url) {
                    val request = ImageRequest.Builder(platformContext)
                        .data(model.url)
                        .size(SizeResolver.ORIGINAL)
                        .build()

                    val imageLoader = SingletonImageLoader.get(platformContext)
                    val result = imageLoader.execute(request)

                    result.image?.let {
                        size = Size(
                            width = it.width.toFloat(),
                            height = it.height.toFloat(),
                        )
                    }
                }
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
            size?.let {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier.background(
                            FloconTheme.colorPalette.tertiary,
                            shape = FloconTheme.shapes.extraLarge
                        ).padding(horizontal = 4.dp, vertical = 1.dp),
                        color = FloconTheme.colorPalette.onTertiary,
                        text = "width:${it.width.toInt()}px",
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        modifier = Modifier.background(
                            FloconTheme.colorPalette.tertiary,
                            shape = FloconTheme.shapes.extraLarge
                        ).padding(horizontal = 4.dp, vertical = 1.dp),
                        color = FloconTheme.colorPalette.onTertiary,
                        text = "height:${it.height.toInt()}px",
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
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

package io.github.openflocon.flocondesktop.features.images.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.size.SizeResolver
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.library.designsystem.FloconTheme

/** Request displaying the image with the headers of the original call. */
@Composable
internal fun rememberImageRequest(model: ImagesUiModel): ImageRequest = remember(model) {
    ImageRequest.Builder(PlatformContext.INSTANCE)
        .data(model.url)
        .httpHeaders(
            NetworkHeaders.Builder().apply {
                model.headers.forEach { (key, value) ->
                    set(key, value)
                }
            }.build(),
        )
        .build()
}

/** Decodes the image at its original size, and returns its formatted dimensions once known. */
@Composable
internal fun rememberImageDimensions(model: ImagesUiModel): State<String?> {
    val dimensions = remember(model.url) { mutableStateOf<String?>(null) }
    if (!isInPreview) {
        val platformContext = LocalPlatformContext.current
        LaunchedEffect(model.url) {
            val request = ImageRequest.Builder(platformContext)
                .data(model.url)
                .size(SizeResolver.ORIGINAL)
                .build()
            val result = SingletonImageLoader.get(platformContext).execute(request)
            result.image?.let {
                dimensions.value = "${it.width}×${it.height}"
            }
        }
    }
    return dimensions
}

/** Host (ink, medium) immediately followed by the path (muted), on a single line. */
@Composable
internal fun ImageUrlText(
    host: String,
    path: String,
    modifier: Modifier = Modifier,
) {
    val hostColor = FloconTheme.colors.ink
    val pathColor = FloconTheme.colors.inkMuted
    val text = remember(host, path, hostColor, pathColor) {
        buildAnnotatedString {
            withStyle(SpanStyle(color = hostColor, fontWeight = FontWeight.Medium)) { append(host) }
            withStyle(SpanStyle(color = pathColor)) { append(path) }
        }
    }
    Text(
        text = text,
        style = FloconTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

/** "14:53:18" in ink followed by ".868" in faint. */
@Composable
internal fun ImageTimeText(
    model: ImagesUiModel,
    modifier: Modifier = Modifier,
    main: Color = FloconTheme.colors.ink,
) {
    val faint = FloconTheme.colors.inkFaint
    val text = remember(model.timeMain, model.timeMillis, main, faint) {
        buildAnnotatedString {
            withStyle(SpanStyle(color = main)) { append(model.timeMain) }
            withStyle(SpanStyle(color = faint)) { append(model.timeMillis) }
        }
    }
    Text(
        text = text,
        style = FloconTheme.typography.monoSmall,
        maxLines = 1,
        modifier = modifier,
    )
}

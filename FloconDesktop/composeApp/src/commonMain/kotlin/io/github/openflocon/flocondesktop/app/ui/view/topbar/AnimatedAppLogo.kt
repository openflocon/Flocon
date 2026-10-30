package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image as SkiaImage

private const val AnimatedLogoPath = "files/app_icon_animated.gif"

@Immutable
internal data class GifFrame(
    val image: ImageBitmap,
    val durationMillis: Long,
)

/**
 * The app logo: shows the first frame of the animated dog, and plays the whole animation once on click.
 * Clicks while it plays are ignored.
 */
@Composable
fun AnimatedAppLogo(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val frames by produceState<ImmutableList<GifFrame>?>(initialValue = null) {
        value = runCatching { loadGifFrames(Res.readBytes(AnimatedLogoPath)) }.getOrNull()
    }
    var frameIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        val gif = frames
        if (!isPlaying || gif == null) return@LaunchedEffect
        gif.forEachIndexed { index, frame ->
            frameIndex = index
            delay(frame.durationMillis)
        }
        frameIndex = 0
        isPlaying = false
    }

    val clickModifier = modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Button,
            onClick = { if (frames != null) isPlaying = true },
        )
        .pointerHoverIcon(PointerIcon.Hand)

    val frame = frames?.getOrNull(frameIndex)
    if (frame != null) {
        Image(
            bitmap = frame.image,
            contentDescription = contentDescription,
            modifier = clickModifier,
        )
    } else {
        Image(
            painter = painterResource(Res.drawable.app_icon_small),
            contentDescription = contentDescription,
            modifier = clickModifier,
        )
    }
}

/**
 * Decodes every frame of a GIF. Frames are decoded in order into the same bitmap, so a frame that only
 * redraws part of the picture is drawn over the previous one.
 */
internal suspend fun loadGifFrames(bytes: ByteArray): ImmutableList<GifFrame> = withContext(Dispatchers.Default) {
    val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
    val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
    codec.framesInfo.mapIndexed { index, info ->
        codec.readPixels(bitmap, index, index - 1)
        GifFrame(
            image = SkiaImage.makeFromBitmap(bitmap).toComposeImageBitmap(),
            durationMillis = info.duration.toLong(),
        )
    }.toImmutableList()
}

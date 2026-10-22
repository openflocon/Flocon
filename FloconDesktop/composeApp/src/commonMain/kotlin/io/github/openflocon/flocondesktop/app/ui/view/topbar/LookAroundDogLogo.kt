package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_logo_content_description
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import io.github.openflocon.library.designsystem.FloconTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * The dog logo. Its eyes and head follow the mouse.
 *
 * The lottie is a [LookGridSize] x [LookGridSize] grid of looking directions, one frame per cell:
 * `frame = row * LookGridSize + col`, col = horizontal direction (0 = left), row = vertical direction (0 = up).
 *
 * @param mouseWindowPosition mouse position in window coordinates, read lazily to avoid recomposing on every move.
 */
@Composable
fun LookAroundDogLogo(
    mouseWindowPosition: () -> Offset?,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/dog_lookaround.json").decodeToString())
    }
    var center by remember { mutableStateOf<Offset?>(null) }
    // look direction, each axis in -1..1, animated so the eyes glide instead of jumping
    val lookX = remember { Animatable(0f) }
    val lookY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            val mouse = mouseWindowPosition()
            val c = center
            if (mouse != null && c != null) lookDirection(mouse.x - c.x, mouse.y - c.y) else Offset.Zero
        }.collectLatest { target ->
            coroutineScope {
                launch { lookX.animateTo(target.x, LookSpring) }
                launch { lookY.animateTo(target.y, LookSpring) }
            }
        }
    }

    Image(
        modifier = modifier
            .onGloballyPositioned { center = it.boundsInWindow().center },
        painter = rememberLottiePainter(
            composition = composition,
            progress = { lookProgress(lookX.value, lookY.value) },
        ),
        contentDescription = stringResource(Res.string.app_logo_content_description),
    )
}

internal const val LookGridSize = 15

/** Distance (px) from the logo at which the dog looks fully in that direction. */
private const val LookReachPx = 450f

private val LookSpring = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)

/** Mouse offset from the logo center (screen coordinates, y down) -> look direction, each axis clamped to -1..1. */
internal fun lookDirection(dx: Float, dy: Float): Offset = Offset(
    x = (dx / LookReachPx).coerceIn(-1f, 1f),
    y = (dy / LookReachPx).coerceIn(-1f, 1f),
)

/** Look direction (-1..1 per axis) -> frame index in the grid. */
internal fun lookFrame(x: Float, y: Float): Int {
    val last = LookGridSize - 1
    val col = ((x.coerceIn(-1f, 1f) + 1f) / 2f * last).roundToInt()
    val row = ((y.coerceIn(-1f, 1f) + 1f) / 2f * last).roundToInt()
    return row * LookGridSize + col
}

/** Lottie progress landing in the middle of the frame, so rounding never spills into the next grid cell. */
internal fun lookProgress(x: Float, y: Float): Float =
    (lookFrame(x, y) + 0.5f) / (LookGridSize * LookGridSize)

@Preview
@Composable
private fun LookAroundDogLogoPreview() {
    FloconTheme {
        LookAroundDogLogo(
            mouseWindowPosition = { null },
            modifier = Modifier.size(40.dp),
        )
    }
}

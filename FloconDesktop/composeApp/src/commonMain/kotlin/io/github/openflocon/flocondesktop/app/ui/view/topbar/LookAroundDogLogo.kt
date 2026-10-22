package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.animation.core.Animatable
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
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource

/**
 * The dog logo. It looks toward the mouse: the lottie is one 360° look
 * (left, top, right, bottom, left), so its progress is the angle between the logo and the mouse.
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
    // unwrapped progress: keeps turning the shortest way instead of sweeping back across the left seam
    val progress = remember { Animatable(LookLeftProgress) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            val mouse = mouseWindowPosition()
            val c = center
            if (mouse != null && c != null) lookAroundProgress(mouse.x - c.x, mouse.y - c.y) else null
        }.collectLatest { target ->
            if (target == null) return@collectLatest
            val current = progress.value
            progress.animateTo(current + shortestDelta(from = current.wrapProgress(), to = target))
        }
    }

    Image(
        modifier = modifier
            .onGloballyPositioned { center = it.boundsInWindow().center },
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress.value.wrapProgress() },
        ),
        contentDescription = stringResource(Res.string.app_logo_content_description),
    )
}

private const val LookLeftProgress = 0f

/**
 * Lottie progress (0 = left, 0.25 = top, 0.5 = right, 0.75 = bottom) for a mouse offset in screen coordinates (y down)
 * relative to the logo center.
 */
internal fun lookAroundProgress(dx: Float, dy: Float): Float {
    if (dx == 0f && dy == 0f) return LookLeftProgress
    val degrees = atan2(dy, dx) * 180f / PI.toFloat() // right = 0, down = 90, left = ±180, up = -90
    return ((degrees + 180f) / 360f).wrapProgress()
}

internal fun shortestDelta(from: Float, to: Float): Float {
    val delta = to - from
    return when {
        delta > 0.5f -> delta - 1f
        delta < -0.5f -> delta + 1f
        else -> delta
    }
}

internal fun Float.wrapProgress(): Float = this - floor(this)

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

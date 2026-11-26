package io.github.openflocon.flocondesktop.features.proxy.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.proxy_wake_up
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPixelPalette
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val StepMs = 120L

/** Steps the full text stays before it is typed again. */
private const val HoldSteps = 25

/** The cursor blinks every this many steps. */
private const val BlinkSteps = 4
private const val Cursor = "█"

/**
 * While the proxy is off and the mascot sleeps: a green terminal cursor types "Wake up…" in the
 * title bar, letter by letter, holds it, then types it again.
 */
@Composable
internal fun ProxyWakeUpText(modifier: Modifier = Modifier) {
    val text = stringResource(Res.string.proxy_wake_up)
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(text) {
        while (true) {
            delay(StepMs)
            step = (step + 1) % (text.length + HoldSteps)
        }
    }
    val cursorVisible = (step / BlinkSteps) % 2 == 0
    Text(
        // the cursor keeps its place when it blinks off, so the text does not move
        text = text.take(step) + if (cursorVisible) Cursor else " ",
        style = FloconTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            shadow = Shadow(color = FloconPixelPalette.Neon.copy(alpha = 0.6f), blurRadius = 8f),
        ),
        fontWeight = FontWeight.Medium,
        color = FloconPixelPalette.Neon,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        modifier = modifier,
    )
}

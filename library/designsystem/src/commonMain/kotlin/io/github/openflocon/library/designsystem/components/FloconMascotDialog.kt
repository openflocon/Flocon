package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler

private val DialogShape = RoundedCornerShape(10.dp)

/** The paws rest on the top edge of the window: the surface starts a bit below their top. */
private val MascotPawsOverlap = 8.dp
private val MascotEndOffset = 56.dp

/**
 * A dialog with the mascot looking at it from above, its paws on the top edge. The mascot is drawn
 * outside of the surface, which clips its content, so the dialog is built here. Without the mascot
 * (turned off in the settings), it is a plain [FloconDialog].
 *
 * @param mascotPawsY from the top of [mascot] to the top of its paws.
 */
@Composable
fun FloconMascotDialog(
    onDismissRequest: () -> Unit,
    width: Dp,
    mascotEnabled: Boolean,
    mascotPawsY: Dp,
    mascot: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val properties = DialogProperties(usePlatformDefaultWidth = false)
    // FloconDialog has no border, drawn here over the content so that the window stands out
    val bordered: @Composable () -> Unit = {
        Box(modifier = Modifier.border(1.dp, FloconTheme.colors.lineStrong, DialogShape)) {
            content()
        }
    }
    if (!mascotEnabled) {
        FloconDialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
            modifier = modifier.width(width),
            content = bordered,
        )
        return
    }
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        EscapeHandler {
            onDismissRequest()
            true
        }
        Box(modifier = modifier.width(width)) {
            FloconSurface(
                color = FloconTheme.colors.floating,
                shape = DialogShape,
                modifier = Modifier
                    .padding(top = mascotPawsY + MascotPawsOverlap)
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                bordered()
            }
            mascot(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = MascotEndOffset),
            )
        }
    }
}

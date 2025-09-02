package io.github.openflocon.flocondesktop.common.ui.feedback

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.feedback.FeedbackDisplayerHandler
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.koinInject

@Composable
fun BoxScope.FeedbackDisplayerView(modifier: Modifier = Modifier) {
    val displayer: FeedbackDisplayerHandler = koinInject()

    val snackbarHostState = remember { SnackbarHostState() } // Create SnackbarHostState

    val toDisplay by displayer.messagesToDisplay.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(toDisplay) {
        toDisplay?.let {
            snackbarHostState.showSnackbar(it.message)
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier =
        modifier
            .align(Alignment.BottomCenter) // Align to bottom center
            .padding(16.dp) // Padding from the bottom and sides
            .animateContentSize(), // Animate the size change of the snackbar
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = FloconTheme.colorPalette.accent, // Invert surface for contrast
            contentColor = FloconTheme.colorPalette.onAccent, // Invert onSurface for contrast
            shape = RoundedCornerShape(8.dp),
        )
    }
}

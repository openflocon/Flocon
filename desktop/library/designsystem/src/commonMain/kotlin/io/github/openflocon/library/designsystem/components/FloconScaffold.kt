package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = FloconTheme.colorPalette.surface,
    contentColor: Color = FloconTheme.colorPalette.contentColorFor(containerColor),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}

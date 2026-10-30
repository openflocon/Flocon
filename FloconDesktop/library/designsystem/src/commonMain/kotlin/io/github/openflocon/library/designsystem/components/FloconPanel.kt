package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/** The bordered card holding the main content of a screen (list, table, sidebar...), same look as [FloconPageTopBar]. */
@Composable
fun Modifier.floconPanel(): Modifier = this
    .clip(FloconTheme.shapes.medium)
    .background(FloconTheme.colors.bg100)
    .border(width = 1.dp, color = FloconTheme.colors.line, shape = FloconTheme.shapes.medium)

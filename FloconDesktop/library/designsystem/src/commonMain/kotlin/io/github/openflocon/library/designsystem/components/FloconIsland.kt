package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val IslandShape = RoundedCornerShape(20.dp)

/** A rounded block floating above the page background, used for the app chrome (top bar, left panel, content). */
@Composable
fun Modifier.floconIsland(background: Brush): Modifier = this
    .clip(IslandShape)
    .background(background)
    .border(1.dp, FloconTheme.colors.lineStrong, IslandShape)

@Composable
fun Modifier.floconIsland(background: Color): Modifier = floconIsland(SolidColor(background))

package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LineWidth = 1.dp

internal fun Modifier.drawBottomLine(color: Color): Modifier = drawLine(color = color, top = false)

internal fun Modifier.drawTopLine(color: Color): Modifier = drawLine(color = color, top = true)

private fun Modifier.drawLine(color: Color, top: Boolean): Modifier = this.drawWithContent {
    drawContent()
    val thickness = LineWidth.toPx()
    drawRect(
        color = color,
        topLeft = Offset(0f, if (top) 0f else size.height - thickness),
        size = Size(size.width, thickness),
    )
}

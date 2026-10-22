package io.github.openflocon.library.designsystem.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Glassmorphic variant of the palette: the surfaces are translucent and let the
 * [FloconGlassBackground] shine through, the borders are light-catching edges.
 */
val FloconGlassDarkColors = FloconDarkColors.copy(
    bg000 = Color(0x800B0B14),
    bg100 = Color(0x1AFFFFFF),
    bg200 = Color(0x12FFFFFF),
    bg300 = Color(0x29FFFFFF),
    line = Color(0x1FFFFFFF),
    lineStrong = Color(0x38FFFFFF),
    ink = Color(0xFFF5F5FA),
    inkMuted = Color(0xFFC4C4D2),
    inkFaint = Color(0xFF9A9AAE),
    chrome = Color(0xFF241F30),
    chromeTint = Color(0xFF3A2C5C),
)

val FloconGlassLightColors = FloconLightColors.copy(
    bg000 = Color(0x33FFFFFF),
    bg100 = Color(0x66FFFFFF),
    bg200 = Color(0x4DFFFFFF),
    bg300 = Color(0x80FFFFFF),
    line = Color(0x99FFFFFF),
    lineStrong = Color(0x33000000),
    accentSoft = Color(0x4DFCE3B0),
)

/** Soft colored blobs on a gradient: the "scenery" that the glass surfaces blur out. */
@Composable
fun FloconGlassBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val base = if (isDarkTheme) {
        listOf(Color(0xFF080A1E), Color(0xFF100A28), Color(0xFF1A0C24))
    } else {
        listOf(Color(0xFFB8C8EC), Color(0xFFCDBBEA), Color(0xFFEBC6B0))
    }
    val blobs = if (isDarkTheme) {
        listOf(Color(0xFF3B6CFF), Color(0xFFB04BFF), Color(0xFFF5B440))
    } else {
        listOf(Color(0xFF7FB2FF), Color(0xFFD59BFF), Color(0xFFFFC88A))
    }
    val blobAlpha = if (isDarkTheme) 0.38f else 0.55f

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // dark in the bottom left corner, the colors gather in the top right one
                drawRect(
                    Brush.linearGradient(
                        colors = base,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, 0f),
                    ),
                )
                val r = size.maxDimension * 0.55f
                val centers = listOf(
                    Offset(size.width * 0.70f, size.height * 0.00f),
                    Offset(size.width * 1.00f, size.height * 0.05f),
                    Offset(size.width * 0.88f, size.height * 0.30f),
                )
                centers.forEachIndexed { i, c ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(blobs[i].copy(alpha = blobAlpha), Color.Transparent),
                            center = c,
                            radius = r,
                        ),
                        radius = r,
                        center = c,
                    )
                }
            },
    ) {
        content()
    }
}

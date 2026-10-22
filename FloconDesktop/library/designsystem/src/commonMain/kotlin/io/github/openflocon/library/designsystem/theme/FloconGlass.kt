package io.github.openflocon.library.designsystem.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.hypot

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
    islandSidebar = Color(0xC7241F30),
    islandTopBar = Color(0xDB241F30),
    islandTopBarTint = Color(0xDB3A2C5C),
    pageFrom = Color(0xFF0E1230),
    pageMid = Color(0xFF0C0820),
    pageTo = Color(0xFF0A0614),
    // amber in the corner, violet and blue glows along the top right edge
    pageGlowCorner = Color(0x24F5B440),
    pageGlowTop = Color(0x29B04BFF),
    pageGlowSide = Color(0x243B6CFF),
)

val FloconGlassLightColors = FloconLightColors.copy(
    bg000 = Color(0x33FFFFFF),
    bg100 = Color(0x66FFFFFF),
    bg200 = Color(0x4DFFFFFF),
    bg300 = Color(0x80FFFFFF),
    line = Color(0x99FFFFFF),
    lineStrong = Color(0x33000000),
    accentSoft = Color(0x4DFCE3B0),
    islandSidebar = Color(0xC7FFFFFF),
    islandTopBar = Color(0xDBFFFFFF),
    islandTopBarTint = Color(0xDBFFFFFF),
    pageFrom = Color(0xFFB8C8EC),
    pageMid = Color(0xFFCDBBEA),
    pageTo = Color(0xFFEBC6B0),
    pageGlowCorner = Color(0x80FFC88A),
    pageGlowTop = Color(0x80D59BFF),
    pageGlowSide = Color(0x807FB2FF),
)

/** The page gradient and its glows (see the `page*` tokens of [FloconColors]): the "scenery" behind the glass surfaces. */
@Composable
fun FloconGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalFloconColors.current
    val glows = listOf(colors.pageGlowCorner, colors.pageGlowTop, colors.pageGlowSide)

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // dark in the bottom left corner, the colors gather in the top right one
                drawRect(
                    Brush.linearGradient(
                        0f to colors.pageFrom,
                        0.55f to colors.pageMid,
                        1f to colors.pageTo,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, 0f),
                    ),
                )
                val diagonal = hypot(size.width, size.height)
                val centers = listOf(
                    Offset(size.width * 0.98f, 0f),
                    Offset(size.width * 0.80f, 0f),
                    Offset(size.width, size.height * 0.30f),
                )
                val radii = listOf(diagonal * 0.40f, diagonal * 0.48f, diagonal * 0.42f)
                centers.forEachIndexed { i, c ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glows[i], Color.Transparent),
                            center = c,
                            radius = radii[i],
                        ),
                        radius = radii[i],
                        center = c,
                    )
                }
            },
    ) {
        content()
    }
}

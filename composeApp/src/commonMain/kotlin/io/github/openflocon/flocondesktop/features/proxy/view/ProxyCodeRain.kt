package io.github.openflocon.flocondesktop.features.proxy.view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.math.pow

/** Half-width katakana and digits, drawn mirrored. */
private const val Glyphs = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜ0123456789"
private const val TickMs = 90L

/** The frame shown when the rain does not move. */
private const val FrozenTick = 37

private val ColumnWidth = 11.dp
private val RowHeight = 9.dp
private val EdgeFade = 64.dp
private val HeadGlow = 4.dp
private val GlyphStyle = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Medium)

/**
 * Falling code behind the title bar of the proxy dialog: columns of glyphs with a bright head
 * and a fading trail. Slow columns look further back (dimmer), fast ones in front. The rain
 * fades out on the left and right edges. [headColor] lights the head of each column, if any.
 */
@Composable
internal fun ProxyCodeRain(
    color: Color,
    animated: Boolean,
    modifier: Modifier = Modifier,
    headColor: Color? = null,
    strength: Float = 1f,
    seed: Int = 1,
) {
    val measurer = rememberTextMeasurer(cacheSize = Glyphs.length)
    var tick by remember { mutableIntStateOf(FrozenTick) }
    LaunchedEffect(animated) {
        while (animated) {
            delay(TickMs)
            tick++
        }
    }

    Canvas(
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                val fade = (EdgeFade.toPx() / size.width).coerceIn(0f, 0.5f)
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        fade to Color.Black,
                        1f - fade to Color.Black,
                        1f to Color.Transparent,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            },
    ) {
        val columnWidth = ColumnWidth.toPx()
        val rowHeight = RowHeight.toPx()
        val rows = (size.height / rowHeight).toInt()
        val columns = (size.width / columnWidth).toInt()
        for (column in 0 until columns) {
            val hash = (column * 73 + seed * 31) % 97
            if (hash % 7 == 0) continue
            val speed = 1 + hash % 3
            val depth = 0.45f + speed * 0.18f
            val length = 3 + hash % 4
            val head = floor((tick * speed * 0.5f + hash * 7) % (rows + length + 4)).toInt()
            for (index in 0..length) {
                val row = head - index
                if (row !in 0 until rows) continue
                val alpha = if (index == 0) 1f else (1f - index / (length + 1f)).pow(1.4f) * 0.8f
                val base = if (index == 0) headColor ?: color else color
                val glyphColor = base.copy(alpha = (alpha * depth * strength).coerceIn(0f, 1f))
                // a cell changes its glyph now and then, the head flickers at every step
                val flicker = if (index == 0) tick else tick / 8
                val glyph = Glyphs[(column * 31 + row * 17 + flicker * 7 + seed) % Glyphs.length]
                val layout = measurer.measure(glyph.toString(), GlyphStyle)
                val x = column * columnWidth
                val y = row * rowHeight
                withTransform({ scale(-1f, 1f, pivot = Offset(x + columnWidth / 2, y + rowHeight / 2)) }) {
                    drawText(
                        textLayoutResult = layout,
                        color = glyphColor,
                        topLeft = Offset(x + (columnWidth - layout.size.width) / 2, y + (rowHeight - layout.size.height) / 2),
                        shadow = if (index < 2) Shadow(color = glyphColor.copy(alpha = glyphColor.alpha * 0.9f), blurRadius = HeadGlow.toPx()) else null,
                    )
                }
            }
        }
    }
}

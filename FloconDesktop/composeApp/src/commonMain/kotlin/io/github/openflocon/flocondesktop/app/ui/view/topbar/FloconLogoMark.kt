package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.desktop.ui.tooling.preview.Preview
import io.github.openflocon.library.designsystem.FloconTheme

private val LogoMarkSize = 30.dp
private val LogoGlyphSize = 18.dp

/** Accent square holding the Flocon snowflake. */
@Composable
fun FloconLogoMark(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val glyph = remember { snowflake() }

    Box(
        modifier = modifier
            .size(LogoMarkSize)
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.accent),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = glyph,
            contentDescription = contentDescription,
            tint = FloconTheme.colors.accentInk,
            modifier = Modifier.size(LogoGlyphSize),
        )
    }
}

private fun snowflake(): ImageVector = ImageVector.Builder(
    name = "FloconSnowflake",
    defaultWidth = 16.dp,
    defaultHeight = 16.dp,
    viewportWidth = 16f,
    viewportHeight = 16f,
).apply {
    addPath(
        pathData = PathParser()
            .parsePathString("M8 1.5v13M2.4 4.75l11.2 6.5M2.4 11.25l11.2-6.5M6.3 2.6L8 4.2l1.7-1.6M6.3 13.4L8 11.8l1.7 1.6")
            .toNodes(),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.3f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()

@Composable
@Preview
private fun FloconLogoMarkPreview() {
    FloconTheme {
        FloconLogoMark()
    }
}

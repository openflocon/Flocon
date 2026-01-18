package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.LinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import io.github.openflocon.library.designsystem.FloconTheme

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun PerformanceChartView(
    title: String,
    data: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
    maxPoints: Int = 100,
) {
    val points = remember(data) {
        val lastPoints = if (data.size > maxPoints) data.takeLast(maxPoints) else data
        lastPoints.mapIndexed { index, d -> DefaultPoint(index.toFloat(), d.toFloat()) }
    }
    
    //val displayedMaxY = maxY ?: (data.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    
    Column(modifier = modifier) {
        Text(
            text = title,
            style = FloconTheme.typography.titleSmall,
            color = FloconTheme.colorPalette.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            if (points.size >= 2) {
                KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(color = Color.Black, minorGridlineStyle = null)) {
                    XYGraph(
                        rememberFloatLinearAxisModel(points.autoScaleXRange()),
                        rememberFloatLinearAxisModel(points.autoScaleYRange()),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LinePlot(
                            data = points,
                            lineStyle = LineStyle(brush = SolidColor(color), strokeWidth = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

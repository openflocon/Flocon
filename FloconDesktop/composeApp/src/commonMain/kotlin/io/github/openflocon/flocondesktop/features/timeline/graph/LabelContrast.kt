package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.theme.FloconLightColors
import kotlin.math.pow

// the two inks of the labels drawn on a marker color: white and the dark ink of the design system
private val LightInk = FloconLightColors.bg100
private val DarkInk = FloconLightColors.ink

/** WCAG relative luminance of an opaque [color]. */
internal fun relativeLuminance(color: Color): Double {
    fun channel(c: Float): Double = c.toDouble().let { if (it <= 0.03928) it / 12.92 else ((it + 0.055) / 1.055).pow(2.4) }
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}

/** WCAG contrast ratio between two opaque colors, from 1 to 21. */
internal fun contrastRatio(a: Color, b: Color): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
}

/** The text color of a label drawn on [background]: the light or the dark ink, whichever has the higher contrast. */
internal fun readableInkOn(background: Color): Color = if (contrastRatio(background, LightInk) >= contrastRatio(background, DarkInk)) LightInk else DarkInk

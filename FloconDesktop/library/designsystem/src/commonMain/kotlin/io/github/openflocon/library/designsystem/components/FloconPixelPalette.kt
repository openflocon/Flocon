package io.github.openflocon.library.designsystem.components

import androidx.compose.ui.graphics.Color

/**
 * Fixed colors of the 8-bit art: the mascot and the objects of the pixel scenes.
 * Pixel art keeps its own palette whatever the theme.
 */
object FloconPixelPalette {
    // mascot
    val Golden = Color(0xFFF2B544)

    /** A touch darker than the head, so the neck shows. */
    val GoldenBody = Color(0xFFE0A034)
    val GoldenDark = Color(0xFFD99522)
    val GoldenLight = Color(0xFFF9D98B)
    val GoldenPaw = Color(0xFFEBC06A)
    val Ink = Color(0xFF2B2140)
    val Ear = Color(0xFFC9841C)
    val Pink = Color(0xFFFF5C9A)
    val Blush = Color(0xFFFF8FB1)
    val Tongue = Color(0xFFFF7A9C)
    val Sleep = Color(0xFFF5D68B)
    val Shadow = Color(0x40000000)
    val Dust = Color(0x66FFFFFF)
    val White = Color(0xFFFFFFFF)

    // objects
    val Metal = Color(0xFFC9C9D6)
    val MetalDark = Color(0xFF9CA3AF)
    val Cable = Color(0xFF8E8EA0)
    val Device = Color(0xFF4A4A5C)
    val DeviceDark = Color(0xFF33333F)
    val Port = Color(0xFF111118)
    val Screen = Color(0xFF1F2937)
    val ScreenLine = Color(0xFF4B5563)
    val Muted = Color(0xFF6B6B7B)
    val Slot = Color(0xFF3B4A63)
    val Blue = Color(0xFF3AA0FF)
    val BlueLight = Color(0xFF7CC0FF)
    val Green = Color(0xFF22C55E)
    val GreenDark = Color(0xFF15803D)
    val GreenOff = Color(0xFF14532D)
    val Purple = Color(0xFF8B7CF6)
    val PurpleDark = Color(0xFF6D5BD0)
    val PurpleLight = Color(0xFFB3A8FA)
    val Wood = Color(0xFF8E5A2B)
    val WoodDark = Color(0xFF6E4320)
    val Paper = Color(0xFFF5F1E6)
    val PaperDark = Color(0xFFDCD6C4)
    val Folder = Color(0xFFFFC857)
    val FolderDark = Color(0xFFE0A72F)
    val Dirt = Color(0xFF7A5230)
    val DirtDark = Color(0xFF4A3020)
    val Red = Color(0xFFE5484D)
    val Sand = Color(0xFFF2C46D)
    val Glass = Color(0xFFCFE8F5)
    val Spark = Color(0xFFFFD34D)
    val Sky = Color(0xFF9ED8F5)
    val Grass = Color(0xFF7CC47F)
    val RoofDark = Color(0xFFB23A3F)

    /** A photo developing, from dark to [Sky]. */
    val Developing = listOf(Color(0xFF2E2E3A), Color(0xFF5B6475), Color(0xFF8FB8D0), Sky)
}

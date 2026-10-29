package io.github.openflocon.flocondesktop.common.ui.featuredoc.scene

import io.github.openflocon.library.designsystem.components.FloconMascotExpression
import io.github.openflocon.library.designsystem.components.FloconMascotPaw
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Blue
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.BlueLight
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Cable
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Developing
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Device
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.DeviceDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Dirt
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.DirtDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Ear
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Folder
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.FolderDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Glass
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Golden
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Grass
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Green
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GreenDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.GreenOff
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Ink
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Metal
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.MetalDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Muted
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Paper
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.PaperDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Pink
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Port
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Purple
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.PurpleDark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.PurpleLight
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Red
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Sand
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Screen
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.ScreenLine
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Shadow
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Slot
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Spark
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.White
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.Wood
import io.github.openflocon.library.designsystem.components.FloconPixelPalette.WoodDark
import io.github.openflocon.library.designsystem.components.FloconPixelScope
import io.github.openflocon.library.designsystem.components.drawMascotDigging
import io.github.openflocon.library.designsystem.components.drawMascotSitting
import kotlinx.collections.immutable.persistentListOf

// Every scene is drawn in the mascot's grid: the mascot sits on x 0..33, the object stands on its right, the floor is row 24

/** It holds the cable in its mouth and plugs it into the router: the LEDs light up. */
internal val NetworkScene = FeatureSceneSpec(
    frames = persistentListOf(0, 0, 1, 1, 2, 2, 1, 0),
    frameMs = 450L,
) { frame ->
    val plugged = frame == 2
    // router
    pixels(36, 12, 1, 5, DeviceDark)
    pixels(43, 12, 1, 5, DeviceDark)
    pixels(36, 11, 1, 1, Blue)
    pixels(43, 11, 1, 1, Blue)
    pixels(35, 17, 11, 8, DeviceDark)
    pixels(35, 17, 11, 6, Device)
    pixels(35, 19, 1, 2, Port)
    pixels(38, 19, 1, 1, if (plugged) Green else GreenOff)
    pixels(40, 19, 1, 1, if (plugged) Green else Muted)
    pixels(42, 19, 1, 1, if (plugged) Green else Muted)
    pixels(18, 24, 12, 1, Cable)
    drawMascotSitting(
        expression = if (plugged) FloconMascotExpression.Happy else FloconMascotExpression.Neutral,
        headX = if (frame > 0) 1 else 0,
        hold = { mouthX, mouthY ->
            pixels(mouthX, mouthY + 1, 1, 11, Cable)
            pixels(mouthX, mouthY - 1, 3, 2, Metal)
            pixels(mouthX + 3, mouthY - 1, 2, 1, MetalDark)
            pixels(mouthX + 3, mouthY, 1, 1, MetalDark)
        },
    )
    if (plugged) {
        pixels(38, 13, 1, 1, Spark)
        pixels(40, 12, 1, 1, Spark)
    }
}

/** It shakes a polaroid that develops... into a photo of itself. */
internal val ImagesScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 2, 3, 3, 3),
    frameMs = 500L,
) { frame ->
    val developed = frame == 3
    drawMascotSitting(
        expression = if (developed) FloconMascotExpression.Happy else FloconMascotExpression.Neutral,
        headX = frame % 2,
        hold = { mouthX, mouthY ->
            pixels(mouthX, mouthY - 1, 9, 10, Paper)
            pixels(mouthX + 1, mouthY, 7, 6, Developing[frame])
            if (developed) {
                pixels(mouthX + 1, mouthY + 4, 7, 2, Grass)
                pixels(mouthX + 3, mouthY + 1, 3, 3, Golden)
                pixels(mouthX + 4, mouthY + 2, 1, 1, Ink)
                pixels(mouthX + 2, mouthY + 1, 1, 2, Ear)
            }
            pixels(mouthX, mouthY + 8, 9, 1, PaperDark)
        },
    )
    if (developed) {
        pixels(41, 2, 1, 1, White)
        pixels(43, 4, 1, 1, White)
        pixels(40, 5, 1, 1, White)
    }
}

/** It digs a database out of the ground. */
internal val DatabaseScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1),
    frameMs = 220L,
) { frame ->
    // database coming out of the hole
    pixels(30, 16 + frame, 7, 6, Purple)
    pixels(30, 20 + frame, 7, 1, PurpleDark)
    pixels(31, 15 + frame, 5, 1, PurpleLight)
    pixels(31, 18 + frame, 1, 1, PurpleLight)
    pixels(26, 22, 14, 3, DirtDark)
    pixels(27, 21, 12, 1, Dirt)
    drawMascotDigging(digging = frame == 0, tailUp = frame == 1)
    // dirt thrown behind
    if (frame == 0) {
        pixels(2, 16, 2, 2, Dirt)
        pixels(-1, 13, 2, 2, Dirt)
    } else {
        pixels(0, 18, 2, 1, Dirt)
        pixels(3, 14, 1, 1, Dirt)
        pixels(-2, 10, 2, 2, Dirt)
    }
}

/** It flips the switch with its paw: off, then on. */
internal val PreferencesScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 2, 3),
    frameMs = 500L,
) { frame ->
    val on = frame >= 2
    val knobX = if (on) 35 else 30
    pixels(28, 16, 13, 7, if (on) GreenDark else DeviceDark)
    pixels(29, 15, 11, 9, if (on) Green else Device)
    pixels(knobX, 16, 4, 7, White)
    pixels(knobX, 22, 4, 1, PaperDark)
    drawMascotSitting(
        expression = if (frame == 3) FloconMascotExpression.Happy else FloconMascotExpression.Neutral,
        paw = if (frame == 1 || frame == 2) FloconMascotPaw.Raised else FloconMascotPaw.Down,
    )
}

/** It tugs at its bone, stuck in a folder. */
internal val FilesScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 0, 1, 2),
    frameMs = 300L,
) { frame ->
    val tug = if (frame == 1) -1 else 0
    drawMascotSitting(
        expression = if (frame == 2) FloconMascotExpression.Blink else FloconMascotExpression.Neutral,
        headX = tug,
        hold = { mouthX, mouthY -> drawBone(mouthX - 1, mouthY - 1) },
    )
    // folder, leaning when the bone is pulled
    pixels(36 + tug, 8, 4, 2, FolderDark)
    pixels(35 + tug, 10, 11, 14, Folder)
    pixels(35 + tug, 10, 1, 14, FolderDark)
    pixels(37 + tug, 9, 7, 1, Paper)
    pixels(45, 24, 3, 1, Shadow)
}

/** It types on the keyboard to build the dashboard. */
internal val DashboardScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 0, 1, 0, 1, 2, 2),
    frameMs = 180L,
) { frame ->
    // screen
    pixels(33, 3, 14, 12, DeviceDark)
    pixels(34, 4, 12, 10, Screen)
    pixels(35, 6, 5, 1, ScreenLine)
    pixels(35, 8, 7, 1, ScreenLine)
    if (frame == 2) {
        pixels(35, 10, 3, 1, Blue)
        pixels(39, 10, 1, 1, Green)
    } else {
        pixels(35, 10, 2, 1, Green)
    }
    pixels(39, 15, 2, 4, Device)
    pixels(37, 19, 6, 1, Device)
    // keyboard
    pixels(25, 20, 16, 4, DeviceDark)
    pixels(26, 20, 14, 1, Device)
    for (key in 0 until 6) pixels(27 + key * 2, 21, 1, 1, Muted)
    drawMascotSitting(
        headX = 1,
        paw = if (frame == 1) FloconMascotPaw.TypingUp else FloconMascotPaw.Typing,
    )
}

/** It brings the first bar of the chart. */
internal val AnalyticsScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 0, 1, 2, 2),
    frameMs = 450L,
) { frame ->
    val placed = frame == 2
    pixels(36, 24, 12, 1, Muted)
    pixels(36, 8, 1, 16, Muted)
    pixels(38, 21, 3, 3, Slot)
    pixels(42, 21, 3, 3, Slot)
    if (placed) pixels(42, 14, 3, 10, Blue)
    drawMascotSitting(
        expression = if (placed) FloconMascotExpression.Happy else FloconMascotExpression.Neutral,
        tailUp = true,
        headX = if (frame == 1) 1 else 0,
        hold = if (placed) {
            null
        } else {
            { mouthX, mouthY ->
                pixels(mouthX, mouthY - 3, 3, 10, Blue)
                pixels(mouthX, mouthY - 3, 3, 1, BlueLight)
            }
        },
    )
}

/** Adb is not available over wifi: the mascot cries, its tears run down and fall. */
internal val AdbCommanderWifiScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 2, 3),
    frameMs = 400L,
) { frame ->
    drawMascotSitting(
        expression = FloconMascotExpression.Blink,
        // nothing in the mouth, so no tongue
        hold = { _, _ -> },
    )
    // tears: they slide down the cheek, the drops fall further each frame
    pixels(22, 8, 1, 3, BlueLight)
    pixels(24, 9, 1, 2, BlueLight)
    pixels(22, 11 + frame, 1, 1, Blue)
    pixels(21, 13 + frame, 1, 1, BlueLight)
}

/** It pulls the tablecloth to set the table. */
internal val TablesScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1),
    frameMs = 350L,
) { frame ->
    val tug = if (frame == 1) -1 else 0
    // table and its cup
    pixels(36, 13, 12, 2, Wood)
    pixels(37, 15, 2, 9, WoodDark)
    pixels(45, 15, 2, 9, WoodDark)
    pixels(41 + tug, 10, 3, 3, White)
    pixels(44 + tug, 11, 1, 1, White)
    drawMascotSitting(
        headX = tug,
        hold = { mouthX, mouthY ->
            pixels(mouthX, mouthY, 3, 2, Red)
            pixels(mouthX + 2, mouthY + 1, 5, 2, Red)
            pixels(mouthX + 6, mouthY + 1, 1, 1, White)
        },
    )
    // tablecloth
    pixels(34 + tug, 12, 14 - tug, 2, Red)
    pixels(34 + tug, 14, 2, 2, Red)
    pixels(37 + tug, 12, 1, 1, White)
    pixels(41 + tug, 12, 1, 1, White)
    pixels(45 + tug, 12, 1, 1, White)
}

/** Leash in its mouth, it waits for someone to take it somewhere. */
internal val DeeplinksScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1),
    frameMs = 280L,
) { frame ->
    drawMascotSitting(
        tailUp = true,
        headY = -frame,
        hold = { mouthX, mouthY ->
            // handle, then the leash down to the floor and its clip
            pixels(mouthX, mouthY - 1, 4, 1, Red)
            pixels(mouthX + 3, mouthY, 1, 3, Red)
            pixels(mouthX, mouthY + 2, 4, 1, Red)
            pixels(mouthX + 2, mouthY + 3, 1, 21 - mouthY, Red)
            pixels(mouthX + 2, 24, 14, 1, Red)
            pixels(mouthX + 15, 23, 2, 2, Metal)
        },
    )
    if (frame == 1) {
        pixels(36, 3, 1, 2, Spark)
        pixels(38, 5, 2, 1, Spark)
    }
}

/** It waves a flag, ready to roll out the first variant. */
internal val FeatureFlagsScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1),
    frameMs = 260L,
) { frame ->
    drawMascotSitting(
        tailUp = true,
        headX = frame,
        hold = { mouthX, mouthY ->
            pixels(mouthX + 1, mouthY - 13, 1, 16, Metal)
            if (frame == 1) {
                pixels(mouthX + 2, mouthY - 13, 7, 4, Pink)
                pixels(mouthX + 2, mouthY - 9, 5, 1, Pink)
            } else {
                pixels(mouthX + 2, mouthY - 13, 6, 3, Pink)
                pixels(mouthX + 2, mouthY - 10, 8, 2, Pink)
            }
        },
    )
}

/** It turns the hourglass over while waiting for the first event. */
internal val TimelineScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1, 2, 3),
    frameMs = 500L,
) { frame ->
    // hourglass, the sand falling from the top bulb to the bottom one
    pixels(30, 8, 9, 2, Wood)
    pixels(30, 22, 9, 2, Wood)
    pixels(31, 10, 1, 12, WoodDark)
    pixels(37, 10, 1, 12, WoodDark)
    pixels(32, 10, 5, 5, Glass)
    pixels(33, 15, 3, 2, Glass)
    pixels(32, 17, 5, 5, Glass)
    val top = 4 - frame
    val bottom = 1 + frame
    pixels(32, 15 - top, 5, top, Sand)
    pixels(34, 15, 1, 2, Sand)
    pixels(32, 22 - bottom, 5, bottom, Sand)
    drawMascotSitting(
        expression = if (frame == 0) FloconMascotExpression.Blink else FloconMascotExpression.Neutral,
        paw = if (frame == 0) FloconMascotPaw.Raised else FloconMascotPaw.Down,
    )
}

/** It caught the bug: no more crash. */
internal val CrashReporterScene = FeatureSceneSpec(
    frames = persistentListOf(0, 1),
    frameMs = 220L,
) { frame ->
    drawMascotSitting(
        expression = FloconMascotExpression.Happy,
        headX = frame,
        hold = { mouthX, mouthY ->
            pixels(mouthX, mouthY - 1, 6, 4, Green)
            pixels(mouthX + 2, mouthY - 1, 1, 4, GreenDark)
            pixels(mouthX + 6, mouthY, 2, 2, GreenDark)
            pixels(mouthX + 7, mouthY, 1, 1, Ink)
            pixels(mouthX + 1, mouthY + 3, 1, 1, Ink)
            pixels(mouthX + 3, mouthY + 3, 1, 1, Ink)
            pixels(mouthX + 5, mouthY + 3, 1, 1, Ink)
            // wings flapping
            if (frame == 1) pixels(mouthX + 1, mouthY - 4, 4, 3, Glass) else pixels(mouthX, mouthY - 2, 5, 1, Glass)
        },
    )
    pixels(38, 6, 1, 1, Spark)
    pixels(41, 9 - frame, 1, 1, Spark)
    pixels(36, 10 + frame, 1, 1, Spark)
}

private fun FloconPixelScope.drawBone(x: Int, y: Int) {
    pixels(x + 1, y + 1, 5, 1, Paper)
    pixels(x, y, 2, 1, Paper)
    pixels(x, y + 2, 2, 1, Paper)
    pixels(x + 5, y, 2, 1, Paper)
    pixels(x + 5, y + 2, 2, 1, Paper)
}

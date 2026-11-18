package io.github.openflocon.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

/** Shared by [FloconNavigation]: the big dialogs blur what the [hazeBackground] content draws. */
internal val LocalHazeState = compositionLocalOf<HazeState?> { null }

/** Marks the content that the big dialogs blur behind them. No-op outside of [FloconNavigation]. */
@Composable
fun Modifier.hazeBackground(): Modifier {
    val state = LocalHazeState.current ?: return this
    return hazeSource(state)
}

package io.github.openflocon.flocondesktop.app.ui.mascot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * The elements any screen declared with [mascotPlatform], by key, in the order they appeared (the
 * mascot keeps platforms by index, so the order has to stay stable). Bounds are in the root
 * coordinates.
 */
@Stable
class MascotPlatforms {
    var bounds: PersistentMap<Any, Rect> by mutableStateOf(persistentMapOf())
        private set

    internal fun register(key: Any, rect: Rect) {
        if (bounds[key] != rect) bounds = bounds.put(key, rect)
    }

    internal fun unregister(key: Any) {
        bounds = bounds.remove(key)
    }
}

/** Null outside of a scene that hosts the mascot, [mascotPlatform] then does nothing. */
val LocalMascot = staticCompositionLocalOf<MascotPlatforms?> { null }

/**
 * Lets the mascot walk on the top edge of this element, for as long as it is in the composition.
 * [key] tells two elements apart, it defaults to one per call site. [platforms] is the registry
 * that provides [LocalMascot] itself, which cannot read it.
 */
@Composable
fun Modifier.mascotPlatform(
    key: Any = remember { Any() },
    platforms: MascotPlatforms? = LocalMascot.current,
): Modifier {
    if (platforms == null) return this
    DisposableEffect(platforms, key) {
        onDispose { platforms.unregister(key) }
    }
    return onGloballyPositioned { platforms.register(key, it.boundsInRoot()) }
}

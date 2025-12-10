@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.navigation.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.navigation.FloconRoute
import kotlin.uuid.ExperimentalUuidApi

@Immutable
data class WindowScene(
    private val entry: NavEntry<FloconRoute>,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val onBack: () -> Unit
) : OverlayScene<FloconRoute> {

    override val key: Any = entry.contentKey

    override val overlaidEntries: List<NavEntry<FloconRoute>> = previousEntries
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val width = entry.metadata[WindowSceneStrategy.SIZE_WIDTH] as? Number
        val height = entry.metadata[WindowSceneStrategy.SIZE_HEIGHT] as? Number
        val size = if (width != null && height != null) {
            DpSize(width.toInt().dp, height.toInt().dp)
        } else null

        val state = rememberWindowState(
            size = size ?: DpSize(800.dp, 600.dp),
        )
        Window(
            onCloseRequest = onBack,
            state = state,
        ) {
            entry.Content()
        }
    }
}

class WindowSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(entries: List<NavEntry<FloconRoute>>): Scene<FloconRoute>? {
        val entry = entries.last()

        if (entry.metadata[IS_WINDOW] == true) {
            return WindowScene(
                entry = entry,
                previousEntries = entries.dropLast(1),
                onBack = onBack
            )
        }

        return null
    }

    companion object {
        private const val IS_WINDOW = "is_window"
        internal const val SIZE_WIDTH = "SIZE_WIDTH"
        internal const val SIZE_HEIGHT = "SIZE_HEIGHT"

        fun window(size: DpSize? = null) = buildMap {
            put(IS_WINDOW, true)
            size?.let {
                put(SIZE_WIDTH, it.width.value)
                put(SIZE_HEIGHT, it.height.value)
            }
        }
    }
}

package io.github.openflocon.navigation.scene

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import io.github.openflocon.navigation.FloconRoute

class WindowScene(
    private val entry: NavEntry<FloconRoute>,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val onBack: () -> Unit
) : OverlayScene<FloconRoute> {

    override val key: Any = entry.contentKey
    override val overlaidEntries: List<NavEntry<FloconRoute>> = previousEntries
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        Window(
            onCloseRequest = onBack
        ) {
            entry.Content()
        }
    }

}

class WindowSceneStrategy : SceneStrategy<FloconRoute> {

    @Composable
    override fun calculateScene(
        entries: List<NavEntry<FloconRoute>>,
        onBack: (Int) -> Unit
    ): Scene<FloconRoute>? {
        val entry = entries.last()

        if (entry.metadata[IS_WINDOW] == true) {
            return WindowScene(
                entry = entry,
                previousEntries = entries.dropLast(1),
                onBack = { onBack(1) }
            )
        }

        return null
    }

    companion object {
        private const val IS_WINDOW = "is_window"

        fun window() = mapOf(IS_WINDOW to true)
    }

}

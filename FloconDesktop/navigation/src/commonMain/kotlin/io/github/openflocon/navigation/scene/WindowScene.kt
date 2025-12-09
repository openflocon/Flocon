@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.navigation.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.window.Window
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.navigation.FloconRoute
import kotlin.uuid.ExperimentalUuidApi

@Immutable
private data class WindowScene(
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>,
    private val properties: WindowProperties,
    private val onBack: () -> Unit
) : OverlayScene<FloconRoute> {

    override val key: Any = entry.contentKey

    override val overlaidEntries: List<NavEntry<FloconRoute>> = previousEntries
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        Window(
            title = properties.title,
            onCloseRequest = onBack
        ) {
            entry.Content()
        }
    }
}

class WindowSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(entries: List<NavEntry<FloconRoute>>): Scene<FloconRoute>? {
        val entry = entries.last()
        val properties = entry.metadata[WINDOW_KEY] ?: return null

        if (properties is WindowProperties) {
            return WindowScene(
                entry = entry,
                properties = properties,
                previousEntries = entries.dropLast(1),
                onBack = onBack
            )
        }

        return null
    }

    companion object {
        private const val WINDOW_KEY = "is_window"

        fun window(
            title: String = ""
        ): Map<String, Any> = mapOf(
            WINDOW_KEY to WindowProperties(
                title = title
            )
        )
    }
}

private data class WindowProperties(
    val title: String
)

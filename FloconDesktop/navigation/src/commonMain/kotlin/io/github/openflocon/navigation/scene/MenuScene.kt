package io.github.openflocon.navigation.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import io.github.openflocon.navigation.FloconRoute

class MenuScene(
    override val key: String,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>,
    private val menuContent: @Composable () -> Unit,
    private val expander: @Composable (() -> Unit)? = null
) : Scene<FloconRoute> {
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)
    override val content: @Composable (() -> Unit) = {
        Box {
            Row {
                menuContent()
                entry.Content()
            }
            expander?.invoke()
        }
    }

    companion object {
        const val MENU_KEY = "menu"
    }
}

class MenuSceneStrategy(
    private val menuContent: @Composable () -> Unit,
    private val expander: @Composable (() -> Unit)? = null
) : SceneStrategy<FloconRoute> {

    @Composable
    override fun calculateScene(
        entries: List<NavEntry<FloconRoute>>,
        onBack: (Int) -> Unit
    ): Scene<FloconRoute>? {
        val lastEntry = entries.last()

        return if (lastEntry.metadata.containsKey(MenuScene.MENU_KEY) && lastEntry.metadata[MenuScene.MENU_KEY] == true) {
            MenuScene(
                key = MenuScene.MENU_KEY,
                previousEntries = entries.dropLast(1),
                entry = lastEntry,
                menuContent = menuContent,
                expander = expander
            )
        } else {
            null
        }
    }

}

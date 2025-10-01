package io.github.openflocon.navigation.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import io.github.openflocon.navigation.FloconRoute

enum class Menu {
    HOME,
    SETTINGS,
    ABOUT,
}

class MenuScene(
    override val key: String,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>
) : Scene<FloconRoute> {
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)
    override val content: @Composable (() -> Unit) = {
        Row {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(Color.Blue)
            )
            entry.Content()
        }
    }

    companion object {
        const val MENU_KEY = "menu"
    }
}

class MenuSceneStrategy : SceneStrategy<FloconRoute> {

    @Composable
    override fun calculateScene(
        entries: List<NavEntry<FloconRoute>>,
        onBack: (Int) -> Unit
    ): Scene<FloconRoute>? {
        val lastEntry = entries.last()

        return if (lastEntry.metadata.containsKey(MenuScene.MENU_KEY)) {
            MenuScene(
                key = MenuScene.MENU_KEY,
                previousEntries = entries.dropLast(1),
                entry = lastEntry
            )
        } else {
            null
        }
    }

}

package io.github.openflocon.flocondesktop.app

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.navigation.FloconRoute

@Immutable
data class MenuScene(
    val scene: Scene<FloconRoute>,
    val menuContent: @Composable ((expanded: Boolean, onToggleExpanded: () -> Unit) -> Unit),
    val topBarContent: @Composable (() -> Unit)?
) : Scene<FloconRoute> {
    override val key: Any = Unit
    override val entries: List<NavEntry<FloconRoute>> = scene.entries
    override val previousEntries: List<NavEntry<FloconRoute>> = scene.previousEntries

    override val content: @Composable (() -> Unit) = {
        var expanded by remember { mutableStateOf(true) }
        val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)

        FloconScaffold(
            topBar = { topBarContent?.invoke() },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            // the sidebar is flush with the window edge, the content keeps its own margin
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Box(
                    modifier = Modifier
                        .width(width)
                        .fillMaxHeight(),
                ) {
                    menuContent(expanded) { expanded = !expanded }
                }
                Row(modifier = Modifier.weight(1f).fillMaxHeight().padding(FloconTheme.spacing.small)) {
                    scene.content()
                }
            }
        }
    }
}

class MenuSceneStrategy(
    private val menuContent: @Composable (expanded: Boolean, onToggleExpanded: () -> Unit) -> Unit,
    private val topBarContent: @Composable (() -> Unit)? = null
) : SceneDecoratorStrategy<FloconRoute> {

    override fun SceneDecoratorStrategyScope<FloconRoute>.decorateScene(scene: Scene<FloconRoute>): Scene<FloconRoute> {
        if (scene.metadata.containsKey(MENU_KEY)) {
            return MenuScene(
                scene = scene,
                menuContent = menuContent,
                topBarContent = topBarContent
            )
        }

        return scene
    }

    companion object {
        private const val MENU_KEY = "menu_key"

        fun menu() = mapOf(MENU_KEY to MENU_KEY)
    }
}

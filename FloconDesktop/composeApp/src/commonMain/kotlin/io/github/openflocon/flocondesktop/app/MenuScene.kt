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
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.library.designsystem.components.floconIsland
import io.github.openflocon.navigation.FloconRoute

@Immutable
data class MenuScene(
    val scene: Scene<FloconRoute>,
    val menuContent: @Composable ((expanded: Boolean, onToggleExpanded: () -> Unit) -> Unit),
    val topBarContent: @Composable (() -> Unit)?
) : Scene<FloconRoute> by scene {
    override val key: Any = MenuScene::class.qualifiedName!!

    override val content: @Composable (() -> Unit) = {
        var expanded by remember { mutableStateOf(true) }
        val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)

        FloconScaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier.padding(
                        start = FloconTheme.spacing.medium,
                        top = FloconTheme.spacing.medium,
                        end = FloconTheme.spacing.medium,
                    ),
                ) {
                    topBarContent?.invoke()
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            // the sidebar and the content float as islands over the page background
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Box(
                    modifier = Modifier
                        .width(width)
                        .fillMaxHeight()
                        .padding(
                            start = FloconTheme.spacing.medium,
                            top = FloconTheme.spacing.medium,
                            bottom = FloconTheme.spacing.medium,
                        ),
                ) {
                    menuContent(expanded) { expanded = !expanded }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(FloconTheme.spacing.medium),
                ) {
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
        if (scene.metadata[MenuKey] == true || scene.metadata[KeepMenuKey] == true) {
            return MenuScene(
                scene = scene,
                menuContent = menuContent,
                topBarContent = topBarContent
            )
        }

        return scene
    }
}

data object MenuKey : NavMetadataKey<Boolean>
data object KeepMenuKey : NavMetadataKey<Boolean>

fun MetadataScope.menu() {
    put(MenuKey, true)
}

fun MetadataScope.keepMenu() {
    put(KeepMenuKey, true)
}

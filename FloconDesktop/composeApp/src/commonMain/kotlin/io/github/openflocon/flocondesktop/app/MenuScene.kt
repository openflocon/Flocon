package io.github.openflocon.flocondesktop.app

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import io.github.openflocon.flocondesktop.app.ui.mascot.LocalMascot
import io.github.openflocon.flocondesktop.app.ui.mascot.MascotOverlay
import io.github.openflocon.flocondesktop.app.ui.mascot.MascotPlatforms
import io.github.openflocon.flocondesktop.app.ui.mascot.MascotViewModel
import io.github.openflocon.flocondesktop.app.ui.mascot.mascotPlatform
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.library.designsystem.components.floconIsland
import io.github.openflocon.navigation.FloconRoute
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel

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

        val mascotEnabled by koinViewModel<MascotViewModel>().isEnabled.collectAsStateWithLifecycle()
        var origin by remember { mutableStateOf(Offset.Zero) }
        var pointer by remember { mutableStateOf<Offset?>(null) }
        val mascotPlatforms = remember { MascotPlatforms() }
        val mascotMargin = with(LocalDensity.current) { FloconTheme.spacing.medium.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { origin = it.positionInRoot() }
                .pointerInput(Unit) {
                    // watches the pointer without consuming anything
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            event.changes.firstOrNull()?.let { pointer = it.position }
                        }
                    }
                },
        ) {
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
                            )
                            .mascotPlatform(platforms = mascotPlatforms),
                    ) {
                        menuContent(expanded) { expanded = !expanded }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(FloconTheme.spacing.medium)
                            .mascotPlatform(platforms = mascotPlatforms),
                    ) {
                        CompositionLocalProvider(LocalMascot provides mascotPlatforms) {
                            scene.content()
                        }
                    }
                }
            }
            val registered = mascotPlatforms.bounds.values
            if (mascotEnabled && registered.isNotEmpty()) {
                // the mascot walks on the top edge of every element marked with `mascotPlatform`
                // (the sidebar and the content island first, then the ones the screens add), and
                // jumps over the gaps between them
                val platforms = registered
                    .map {
                        mascotPlatform(
                            left = it.left - origin.x,
                            right = it.right - origin.x,
                            top = it.top - origin.y,
                            inset = mascotMargin * 2,
                        )
                    }
                    .toImmutableList()
                MascotOverlay(
                    pointerX = pointer?.x,
                    platforms = platforms,
                )
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

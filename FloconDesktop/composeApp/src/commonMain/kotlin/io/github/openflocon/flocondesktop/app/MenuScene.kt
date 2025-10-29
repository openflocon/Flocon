package io.github.openflocon.flocondesktop.app

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.flocondesktop.app.ui.view.topbar.MainScreenTopBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.navigation.FloconRoute

@Immutable
data class MenuScene(
    override val entries: List<NavEntry<FloconRoute>>,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    val entry: NavEntry<FloconRoute>,
    val menuContent: @Composable (expanded: Boolean) -> Unit
) : Scene<FloconRoute> {
    override val key: Any
        get() = Unit

    override val content: @Composable (() -> Unit) = {
        var expanded by remember { mutableStateOf(false) }
        val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)
        var windowSize by remember { mutableStateOf(IntSize.Zero) }
        val position by animateDpAsState(
            targetValue = if (expanded) PanelMaxWidth else PanelMinWidth,
        )
        val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

        Box {
            FloconScaffold(
                topBar = {
                    MainScreenTopBar(
                        devicesState = DevicesStateUiModel.Empty,
                        appsState = AppsStateUiModel.Empty,
                        recordState = RecordVideoStateUiModel.Recording,
                        deleteApp = {},
                        deleteDevice = {},
                        onDeviceSelected = {},
                        onAppSelected = {},
                        onRecordClicked = {},
                        onRestartClicked = {},
                        onTakeScreenshotClicked = {}
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        windowSize = it.size // TODO Add windowsize lib
                    }
            ) { padding ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(width)
                            .fillMaxHeight(),
                    ) {
                        menuContent(expanded)
                    }
                    entry.Content()
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(20.dp)
                    .height(60.dp)
                    .graphicsLayer {
                        translationX = position.toPx() - size.width / 2 - 2.dp.toPx()
                        translationY = (windowSize.height / 4f).times(3f) + (size.height / 2f)
                    }
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(FloconTheme.colorPalette.secondary)
                    .clickable(onClick = { expanded = !expanded }),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.ChevronRight,
                    tint = Color.LightGray,
                    modifier = Modifier.rotate(rotate),
                )
            }
        }
    }
}

class MenuSceneStrategy(
    private val menuContent: @Composable (expanded: Boolean) -> Unit
) : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(entries: List<NavEntry<FloconRoute>>): Scene<FloconRoute>? {
        val entry = entries.lastOrNull() ?: return null

        if (entry.metadata.containsKey(MENU_KEY)) {
            return MenuScene(
                entries = listOf(entry),
                previousEntries = entries.dropLast(1),
                entry = entry,
                menuContent = menuContent
            )
        }

        return null
    }

    companion object {
        private const val MENU_KEY = "menu_key"

        fun menu() = mapOf(MENU_KEY to MENU_KEY)

    }

}

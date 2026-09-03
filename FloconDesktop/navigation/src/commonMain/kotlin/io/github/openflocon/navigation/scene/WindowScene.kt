@file:OptIn(ExperimentalUuidApi::class, ExperimentalComposeUiApi::class)

package io.github.openflocon.navigation.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.DialogWindow
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.rememberDialogState
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
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
    private val onBack: () -> Unit,
) : OverlayScene<FloconRoute> {

    override val key: Any = entry.contentKey

    override val overlaidEntries: List<NavEntry<FloconRoute>> = previousEntries
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val windowProperties = entry.metadata[WindowPropertiesKey]

        val state = rememberDialogState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = {
                    windowProperties?.size ?: DpSize(800.dp, 600.dp)
                },
                positionProvider = WindowPositionProvider.CenteredInParentWindow,
            ),
        )

        DialogWindow(
            onCloseRequest = onBack,
            state = state,
            title = windowProperties?.title ?: "",
        ) {
            entry.Content()
        }
    }
}

class WindowSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(entries: List<NavEntry<FloconRoute>>): Scene<FloconRoute>? {
        val entry = entries.last()

        if (entry.metadata[WindowPropertiesKey]?.isWindow == true) {
            return WindowScene(
                entry = entry,
                previousEntries = entries.dropLast(1),
                onBack = onBack,
            )
        }

        return null
    }

    companion object {
        fun window(windowProperties: WindowProperties = WindowProperties()) = metadata {
            put(WindowPropertiesKey, windowProperties)
        }
    }
}

data class WindowProperties(
    val size: DpSize? = null,
    val title: String? = null,
) {
    val isWindow: Boolean = true
}

private object WindowPropertiesKey : NavMetadataKey<WindowProperties>

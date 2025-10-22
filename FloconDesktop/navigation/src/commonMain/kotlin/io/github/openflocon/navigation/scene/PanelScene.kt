package io.github.openflocon.navigation.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import io.github.openflocon.library.designsystem.components.panel.FloconPanel
import io.github.openflocon.navigation.FloconRoute
import org.koin.core.component.KoinComponent

class PanelScene(
    override val overlaidEntries: List<NavEntry<FloconRoute>>,
    override val key: Any,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>,
    private val properties: PaneProperties,
    private val onPin: OnPin?,
    private val onBack: (count: Int) -> Unit,
) : OverlayScene<FloconRoute>, KoinComponent {

    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            FloconPanel(
                expanded = true,
                onClose = { onBack(1) },
                onPin = { onPin?.onPin(); onBack(1) }
            ) {
                entry.Content()
            }
        }
    }

}

class PanelSceneStrategy() : SceneStrategy<FloconRoute> {

    @Composable
    override fun calculateScene(
        entries: List<NavEntry<FloconRoute>>,
        onBack: (Int) -> Unit
    ): Scene<FloconRoute>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val properties = lastEntry.metadata[PANEL_KEY] ?: return null

        if (properties is PaneProperties) {
            return PanelScene(
                key = lastEntry.contentKey,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                entry = lastEntry,
                properties = properties,
                onPin = lastEntry.metadata[ON_PIN] as? OnPin,
                onBack = onBack
            )
        }

        return null
    }

    companion object {
        private const val PANEL_KEY = "panel_key"
        private const val ON_PIN = "on_pin"

        fun panel(
            pinnable: Boolean,
            closable: Boolean,
            onPin: OnPin = OnPin.Empty
        ): Map<String, Any> = mapOf(
            PANEL_KEY to PaneProperties(
                pinnable = pinnable,
                closable = closable
            ),
            ON_PIN to onPin
        )

    }

}

fun interface OnPin {

    fun onPin()

    companion object {
        val Empty = OnPin {}
    }

}

data class PaneProperties(
    val pinnable: Boolean = false,
    val closable: Boolean = false
)

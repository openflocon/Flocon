package io.github.openflocon.navigation.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.panel.FloconPanel
import io.github.openflocon.library.designsystem.components.panel.rememberFloconPanelState
import io.github.openflocon.navigation.FloconRoute
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@Immutable
data class PanelScene(
    override val overlaidEntries: List<NavEntry<FloconRoute>>,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>,
    private val properties: PaneProperties,
    private val onPin: OnPin?,
    private val onBack: () -> Unit,
) : OverlayScene<FloconRoute>, KoinComponent {
    override val key: Any
        get() = PanelScene::class.qualifiedName!!

    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val state = rememberFloconPanelState(initialValue = true)
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            FloconPanel(
                state = state,
                onDismissRequest = onBack,
                actions = {
                    FloconIconTonalButton(
                        onClick = {
                            scope.launch {
                                state.hide()
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .animatePanelAction()
                    ) {
                        FloconIcon(
                            Icons.Outlined.Close
                        )
                    }
                    if (onPin != null) {
                        FloconIconTonalButton(
                            onClick = {
                                scope.launch {
                                    onPin.onPin()
                                    state.hide()
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .animatePanelAction()
                        ) {
                            FloconIcon(
                                Icons.Sharp.PushPin
                            )
                        }
                    }
                }
            ) {
                entry.Content()
            }
        }
    }

}

class PanelSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(
        entries: List<NavEntry<FloconRoute>>
    ): Scene<FloconRoute>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val properties = lastEntry.metadata[PANEL_KEY] ?: return null

        if (properties is PaneProperties) {
            return PanelScene(
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
        private val PANEL_KEY = PanelSceneStrategy::class.qualifiedName!!

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

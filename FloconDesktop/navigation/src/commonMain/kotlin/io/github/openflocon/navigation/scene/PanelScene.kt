package io.github.openflocon.navigation.scene

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.enterEnd
import io.github.openflocon.library.designsystem.components.exitEnd
import io.github.openflocon.library.designsystem.components.panel.FloconPanel
import io.github.openflocon.library.designsystem.components.panel.FloconPanelState
import io.github.openflocon.library.designsystem.components.panel.PANEL_WIDTH
import io.github.openflocon.library.designsystem.components.panel.rememberFloconPanelState
import io.github.openflocon.navigation.FloconRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@Immutable
private data class PanelScene(
    override val key: Any,
    private val allEntries: List<NavEntry<FloconRoute>>,
    private val entry: NavEntry<FloconRoute>,
    private val properties: PaneProperties,
    private val onBack: () -> Unit
) : Scene<FloconRoute>,
    KoinComponent {

    private val mainEntry = allEntries.last { !it.metadata.contains(PanelKey) }
    private val panelEntry = allEntries.last { it.metadata.contains(PanelKey) }

    override val previousEntries: List<NavEntry<FloconRoute>> =
        allEntries.filter { it.metadata.contains(PanelKey) }
            .dropLast(1)

    override val entries: List<NavEntry<FloconRoute>> = listOfNotNull(mainEntry, panelEntry)

    private var pinned by mutableStateOf(false)

    override val content: @Composable (() -> Unit) = {
        val state = rememberFloconPanelState(initialValue = true)
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            PinnedPanel()
            SidePanel(
                scope = scope,
                state = state
            )
        }
    }

    @Composable
    private fun PinnedPanel() {
        Row {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize()
            ) {
                mainEntry.Content()
            }
            AnimatedContent(
                targetState = pinned,
                transitionSpec = {
                    enterEnd() togetherWith exitEnd() using SizeTransform()
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                if (it) {
                    Row {
                        Spacer(Modifier.width(FloconTheme.spacing.small))
                        Box(
                            modifier = Modifier
                                .width(PANEL_WIDTH)
                                .fillMaxHeight()
                        ) {
                            panelEntry.Content()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BoxScope.SidePanel(
        scope: CoroutineScope,
        state: FloconPanelState
    ) {
        FloconPanel(
            state = state,
            onDismissRequest = { },
            actions = {
                FloconIconTonalButton(
                    onClick = {
                        scope.launch {
                            state.hide()
                        }
                    },
                    modifier = Modifier.animatePanelAction()
                ) {
                    FloconIcon(
                        Icons.Outlined.Close
                    )
                }
                if (properties.onPin != null) { // TODO Use only one
                    FloconIconTonalButton(
                        onClick = {
                            scope.launch {
                                //properties.onPin.onPin()
                                state.hide()
                                pinned = true
                            }
                        },
                        modifier = Modifier.animatePanelAction()
                    ) {
                        FloconIcon(
                            Icons.Sharp.PushPin
                        )
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Crossfade(
                targetState = entry
            ) { target ->
                if (state.expanded) {
                    target.Content()
                }
            }
        }
    }
}

@Stable
class PanelSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(
        entries: List<NavEntry<FloconRoute>>
    ): Scene<FloconRoute>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val properties = lastEntry.metadata[PanelKey] ?: return null

        return PanelScene(
            key = Unit,
            allEntries = entries,
            entry = lastEntry,
            properties = properties,
            onBack = onBack
        )
    }

    companion object {

        fun MetadataScope.panel(
            closable: Boolean = true,
            onPin: OnPin? = null
        ) {
            put(
                PanelKey, PaneProperties(
                    onPin = onPin,
                    closable = closable
                )
            )
        }
    }
}

data object PanelKey : NavMetadataKey<PaneProperties>

fun interface OnPin {

    fun onPin()

}

private data class PaneProperties(
    val closable: Boolean = false,
    val onPin: OnPin? = null
)

@file:OptIn(ExperimentalDeferredTransitionApi::class)

package io.github.openflocon.navigation.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.ExperimentalDeferredTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler
import io.github.openflocon.navigation.FloconRoute
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@Immutable
private data class PanelScene(
    override val key: Any,
    private val allEntries: List<NavEntry<FloconRoute>>,
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

    enum class States {
        IDLE,
        ENTER,
        EXIT
    }

    override val content: @Composable (() -> Unit) = {
        val scope = rememberCoroutineScope()
        val onPin = properties.onPinHandler?.invoke()
        val pinnable = remember(onPin) { onPin != null }
        val isPinned by onPin?.isPinned
            ?.collectAsStateWithLifecycle()
            ?: mutableStateOf(false)
        val transitionState = remember { MutableTransitionState(States.IDLE to isPinned) }
        val transition = rememberTransition(transitionState)

        LaunchedEffect(isPinned) {
            transitionState.targetState = States.ENTER to isPinned
        }

        LaunchedEffect(transitionState.currentState) {
            if (transitionState.currentState.first == States.EXIT) {
                onBack()
            }
        }

        fun close() {
            transitionState.targetState = States.EXIT to false
        }

        EscapeHandler {
            close()
            true
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    mainEntry.Content()
                }
                transition.AnimatedVisibility(
                    visible = { (state, pinned) ->
                        pinned && when (state) {
                            States.EXIT,
                            States.IDLE -> false

                            States.ENTER -> true
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(PANEL_WIDTH)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
            ) {
                Row {
                    transition.AnimatedVisibility(
                        enter = fadeIn() + slideInVertically { -it },
                        exit = fadeOut() + slideOutVertically { -it },
                        visible = { (state, pinned) ->
                            !pinned && when (state) {
                                States.EXIT,
                                States.IDLE -> false

                                States.ENTER -> true
                            }
                        }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                            modifier = Modifier.padding(FloconTheme.spacing.small),
                            content = {
                                FloconIconTonalButton(
                                    onClick = ::close
                                ) {
                                    FloconIcon(
                                        Icons.Outlined.Close
                                    )
                                }
                                if (pinnable) {
                                    FloconIconTonalButton(
                                        onClick = { scope.launch { onPin?.onPin(true) } }
                                    ) {
                                        FloconIcon(
                                            Icons.Sharp.PushPin
                                        )
                                    }
                                }
                            }
                        )
                    }
                    transition.AnimatedVisibility(
                        enter = slideInHorizontally { it },
                        exit = slideOutHorizontally { it },
                        visible = { (state, pinned) ->
                            when (state) {
                                States.EXIT,
                                States.IDLE -> false

                                States.ENTER -> true
                            }
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Crossfade(
                            targetState = panelEntry,
                            animationSpec = tween(500),
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(PANEL_WIDTH)
                                .background(FloconTheme.colors.floating)
                        ) { target ->
                            target.Content()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val PANEL_WIDTH = 500.dp
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
            properties = properties,
            onBack = onBack
        )
    }

    companion object {

        fun MetadataScope.panel(
            closable: Boolean = true,
            onPinHandler: @Composable (() -> OnPin)? = null
        ) {
            put(
                PanelKey,
                PaneProperties(
                    closable = closable,
                    onPinHandler = onPinHandler
                )
            )
        }
    }
}

data object PanelKey : NavMetadataKey<PaneProperties>

interface OnPin {

    val isPinned: StateFlow<Boolean>

    fun onPin(pinned: Boolean)
}

private data class PaneProperties(
    val closable: Boolean = false,
    val onPinHandler: @Composable (() -> OnPin)? = null
)

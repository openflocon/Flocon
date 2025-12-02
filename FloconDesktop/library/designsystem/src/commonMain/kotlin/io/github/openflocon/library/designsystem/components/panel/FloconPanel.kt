package io.github.openflocon.library.designsystem.components.panel

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler
import kotlinx.coroutines.launch

private const val ANIM_DURATION = 500
val PANEL_WIDTH = 500.dp

@Stable
class FloconPanelState internal constructor(initialValue: Boolean) {

    internal var expanded by mutableStateOf(initialValue)

    val translationX = AnimationState(typeConverter = Dp.VectorConverter, PANEL_WIDTH)

    suspend fun show() {
        expanded = true
        translationX.animateTo(0.dp, animationSpec = tween(ANIM_DURATION, easing = EaseOutExpo))
    }

    suspend fun hide() {
        translationX.animateTo(PANEL_WIDTH, animationSpec = tween(ANIM_DURATION, easing = EaseOutExpo))
        expanded = false
    }
}

@Composable
fun rememberFloconPanelState(initialValue: Boolean = false): FloconPanelState = remember {
    println("rememberFloconPanelState")
    FloconPanelState(initialValue)
}

interface FloconPanelScope {
    val state: FloconPanelState

    fun Modifier.animatePanelAction(): Modifier
}

private class FloconPanelScopeImpl(
    override val state: FloconPanelState
) : FloconPanelScope {

    @Stable
    override fun Modifier.animatePanelAction(): Modifier = graphicsLayer {
        this.alpha = 1f - state.translationX.value.div(PANEL_WIDTH)
    }
}

@Composable
fun FloconPanel(
    state: FloconPanelState,
    onDismissRequest: () -> Unit,
    actions: @Composable FloconPanelScope.() -> Unit = {},
    content: @Composable FloconPanelScope.() -> Unit
) {
    val scope = remember(state) { FloconPanelScopeImpl(state) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (state.expanded) {
            state.show()
        }
    }

    LaunchedEffect(state.expanded) {
        if (!state.expanded) {
            onDismissRequest()
        }
    }

    EscapeHandler {
        coroutineScope.launch {
            state.hide()
            onDismissRequest()
        }
        true
    }

    Row(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
            content = { scope.actions() }
        )
        Box(
            modifier = Modifier
                .width(PANEL_WIDTH)
                .fillMaxHeight()
                .graphicsLayer {
                    this.translationX = state.translationX.value.toPx()
                }
                .border(width = 1.dp, color = FloconTheme.colorPalette.surface)
        ) {
            scope.content()
        }
    }
}

// TODO Rework
@Composable
fun FloconPanel(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    actions: @Composable FloconPanelScope.() -> Unit = {},
    content: @Composable FloconPanelScope.() -> Unit
) {
    var innerExpand by remember { mutableStateOf(expanded) }
    val state = rememberFloconPanelState(expanded)
    val scope = rememberCoroutineScope()

    LaunchedEffect(expanded) {
        if (expanded) {
            innerExpand = true
            state.show()
        } else {
            state.hide()
            innerExpand = false
        }
    }

    if (innerExpand) {
        FloconPanel(
            state = state,
            onDismissRequest = {
                scope.launch {
                    state.hide()
                    innerExpand = false
                    onDismissRequest()
                }
            },
            actions = actions,
            content = content
        )
    }
}

@Composable
fun <T : Any?> FloconPanel(
    contentState: T,
    onClose: (() -> Unit)? = null,
    content: @Composable FloconPanelScope.(T & Any) -> Unit
) {
    var rememberTarget by remember { mutableStateOf(contentState) }

    LaunchedEffect(contentState) {
        if (contentState != null && contentState != rememberTarget) {
            rememberTarget = contentState
        }
    }

    FloconPanel(
        expanded = contentState != null,
        onDismissRequest = { onClose?.invoke() },
        actions = {
            if (onClose != null) {
                FloconIconTonalButton(
                    onClick = onClose,
                    modifier = Modifier
                ) {
                    FloconIcon(
                        Icons.Outlined.Close
                    )
                }
            }
        },
    ) {
        rememberTarget?.let { content(this, it) }
    }
}

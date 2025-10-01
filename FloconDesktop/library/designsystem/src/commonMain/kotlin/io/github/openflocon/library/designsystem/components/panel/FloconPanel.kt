package io.github.openflocon.library.designsystem.components.panel

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler
import io.github.openflocon.library.designsystem.components.panel.LocalFloconPanelController

private const val AnimDuration = 500
private val PanelWidth = 500.dp

@Composable
fun FloconPanel(
    expanded: Boolean,
    onClose: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var innerExpanded by remember { mutableStateOf(expanded) }
    val translationX = remember { AnimationState(typeConverter = Dp.VectorConverter, PanelWidth) }

    suspend fun hide() {
        translationX.animateTo(PanelWidth, animationSpec = tween(AnimDuration, easing = EaseOutExpo))
        innerExpanded = false
    }

    LaunchedEffect(expanded) {
        if (expanded) {
            innerExpanded = true
            translationX.animateTo(0.dp, animationSpec = tween(AnimDuration, easing = EaseOutExpo))
        } else {
            hide()
        }
    }

    val floconPanelController = LocalFloconPanelController.current

    if (innerExpanded) {
        DisposableEffect(Unit) {
            floconPanelController.display {
                if (onClose != null) {
                    EscapeHandler {
                        onClose()
                        true
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    if (onClose != null) {
                        Box(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            FloconIconTonalButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .graphicsLayer {
                                        this.alpha = 1f - translationX.value.div(PanelWidth)
                                    }
                            ) {
                                FloconIcon(
                                    Icons.Outlined.Close
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(PanelWidth)
                            .fillMaxHeight()
                            .graphicsLayer {
                                this.translationX = translationX.value.toPx()
                            }
                            .border(width = 1.dp, color = FloconTheme.colorPalette.surface),
                        content = content
                    )
                }
            }
            onDispose { floconPanelController.hide() }
        }
    }
}

@Composable
fun <T : Any?> FloconPanel(
    contentState: T,
    onClose: (() -> Unit)? = null,
    content: @Composable BoxScope.(T & Any) -> Unit
) {
    var rememberTarget by remember { mutableStateOf(contentState) }

    LaunchedEffect(contentState) {
        if (contentState != null && contentState != rememberTarget) {
            rememberTarget = contentState
        }
    }

    FloconPanel(
        expanded = contentState != null,
        onClose = onClose,
    ) {
        rememberTarget?.let { content(this, it) }
    }
}

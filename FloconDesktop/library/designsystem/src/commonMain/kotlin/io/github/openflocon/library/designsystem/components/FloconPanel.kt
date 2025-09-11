package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.coroutines.launch

private const val AnimDuration = 500

@Composable
fun <T : Any?> FloconPanel(
    contentState: T,
    modifier: Modifier = Modifier,
    fromStart: Boolean = false,
    content: @Composable AnimatedContentScope.(T & Any) -> Unit
) {
    AnimatedContent(
        targetState = contentState,
        transitionSpec = {
            val tween = tween<IntOffset>(AnimDuration, easing = EaseOutExpo)

            if (fromStart) {
                slideIntoContainer(SlideDirection.End, tween)
                    .togetherWith(slideOutOfContainer(SlideDirection.Start, tween))
            } else {
                slideIntoContainer(SlideDirection.Start, tween)
                    .togetherWith(slideOutOfContainer(SlideDirection.End, tween))
            }
        },
        contentAlignment = Alignment.TopEnd,
        contentKey = { it != null },
        modifier = modifier
            .fillMaxHeight()
            .requiredWidth(500.dp),
        content = {
            if (it != null) {
                content(it)
            } else {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    )
}

private val PanelWidth = 500.dp

@Composable
fun FloconPanel(
    expanded: Boolean,
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var innerExpanded by remember { mutableStateOf(expanded) }
    val translationX = remember { AnimationState(typeConverter = Dp.VectorConverter, PanelWidth) }
    val scope = rememberCoroutineScope()

    suspend fun hide() {
        translationX.animateTo(PanelWidth, animationSpec = tween(AnimDuration, easing = EaseOutExpo))
        innerExpanded = false
        onDismissRequest?.invoke()
    }

    LaunchedEffect(expanded) {
        if (expanded) {
            innerExpanded = true
            translationX.animateTo(0.dp, animationSpec = tween(AnimDuration, easing = EaseOutExpo))
        } else {
            hide()
        }
    }

    if (innerExpanded) {
        Popup(
            onDismissRequest = {
                if (onDismissRequest != null) {
                    scope.launch { hide() }
                }
            },
            alignment = Alignment.CenterEnd
        ) {
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
}

@Composable // TODO Rename
fun <T : Any?> FloconPanelNew(
    contentState: T,
    content: @Composable BoxScope.(T & Any) -> Unit
) {
    var rememberTarget by remember { mutableStateOf(contentState) }

    LaunchedEffect(contentState) {
        if (contentState != null && contentState != rememberTarget) {
            rememberTarget = contentState
        }
    }

    FloconPanel(
        expanded = contentState != null
    ) {
        rememberTarget?.let { content(this, it) }
    }
}

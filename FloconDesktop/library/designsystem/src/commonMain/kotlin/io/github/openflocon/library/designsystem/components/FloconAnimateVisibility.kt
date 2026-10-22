package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun <T> AnimatedContentTransitionScope<T>.enterEnd() = fadeIn(
    animationSpec = tween(220, delayMillis = 90)
)
    .plus(
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(220, delayMillis = 90)
        )
    )

fun <T> AnimatedContentTransitionScope<T>.exitEnd() = fadeOut(
    animationSpec = tween(220, delayMillis = 90)
)
    .plus(
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = tween(220, delayMillis = 90)
        )
    )

@Composable
fun <T> FloconAnimateVisibility(
    state: T?,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<T?>.() -> ContentTransform = {
        enterEnd().togetherWith(fadeOut(animationSpec = tween(90)))
    },
    content: @Composable AnimatedContentScope.(T & Any) -> Unit
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = transitionSpec,
        contentKey = { it != null },
        modifier = modifier
    ) {
        if (it != null) {
            content(it)
        } else {
            Box(Modifier)
        }
    }
}

package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> FloconAnimateVisibility(
    state: T?,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<T?>.() -> ContentTransform = {
        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(220, delayMillis = 90)
            )
            )
            .togetherWith(fadeOut(animationSpec = tween(90)))
    },
    content: @Composable AnimatedContentScope.(T & Any) -> Unit
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = transitionSpec,
        modifier = modifier
    ) {
        if (it != null) {
            content(it)
        }
    }
}

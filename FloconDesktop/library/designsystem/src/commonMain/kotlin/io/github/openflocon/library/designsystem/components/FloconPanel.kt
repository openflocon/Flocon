package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private const val AnimDuration = 300

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

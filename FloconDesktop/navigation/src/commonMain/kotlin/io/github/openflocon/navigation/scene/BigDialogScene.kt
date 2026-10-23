@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.navigation.scene

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.navigation.FloconRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Immutable
private data class BigDialogScene(
    private val entry: NavEntry<FloconRoute>,
    private val properties: BigDialogProperties,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    override val overlaidEntries: List<NavEntry<FloconRoute>>,
    private val onBack: () -> Unit,
) : OverlayScene<FloconRoute> {

    private val spec = tween<Float>(durationMillis = 300, easing = EaseInOutCubic)
    private val alpha = Animatable(initialValue = 0f)
    private val scale = Animatable(initialValue = 0.9f)

    override val key: Any = BigDialogSceneStrategy.BIG_DIALOG
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)
    override val content: @Composable (() -> Unit) = {
        LaunchedEffect(Unit) {
            val alphaTask = async { alpha.animateTo(1f, spec) }
            val scaleTask = async { scale.animateTo(1f, spec) }

            awaitAll(alphaTask, scaleTask)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = this@BigDialogScene.alpha.value
                }
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    onClick = onBack,
                    indication = null,
                    interactionSource = null
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(fraction = 0.8f)
                    .graphicsLayer {
                        this.scaleX = scale.value
                        this.scaleY = scale.value
                    }
                    .dropShadow(
                        shape = RoundedCornerShape(12.dp),
                        shadow = Shadow(
                            radius = 4.dp,
                            color = Color.Black.copy(alpha = 0.35f)
                        )
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colors.floating)
                    .border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(12.dp))
                    .padding(FloconTheme.spacing.large)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = properties.title,
                        color = FloconTheme.colors.ink,
                        fontWeight = FontWeight.SemiBold,
                        style = FloconTheme.typography.headlineSmall
                    )
                    FloconIconButton(
                        onClick = onBack
                    ) {
                        FloconIcon(
                            imageVector = Icons.Default.Close,
                            tint = FloconTheme.colors.inkFaint
                        )
                    }
                }
                Spacer(Modifier.height(FloconTheme.spacing.small))
                HorizontalDivider(color = FloconTheme.colors.line)
                Spacer(Modifier.height(FloconTheme.spacing.small))
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(onClick = {}, indication = null, interactionSource = null)
                ) {
                    entry.Content()
                }
            }
        }
    }

    override suspend fun onRemove() {
        coroutineScope {
            val alphaTask = async { alpha.animateTo(0f, spec) }
            val scaleTask = async { scale.animateTo(0.9f, spec) }

            awaitAll(alphaTask, scaleTask)
        }
    }
}

data class BigDialogProperties(
    val title: String
)

class BigDialogSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(
        entries: List<NavEntry<FloconRoute>>
    ): Scene<FloconRoute>? {
        val lastEntry = entries.lastOrNull()
        val dialogProperties = lastEntry?.metadata?.get(BIG_DIALOG) as? BigDialogProperties
        return dialogProperties?.let { properties ->
            BigDialogScene(
                entry = lastEntry,
                properties = properties,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                onBack = onBack,
            )
        }
    }

    companion object {
        const val BIG_DIALOG = "BIG_DIALOG"

        fun bigDialog(properties: BigDialogProperties): Map<String, Any> = mapOf(BIG_DIALOG to properties)
    }
}

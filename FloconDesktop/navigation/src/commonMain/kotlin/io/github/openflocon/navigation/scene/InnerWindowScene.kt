package io.github.openflocon.navigation.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconSurface

@Immutable
internal data class InnerWindowScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onBack, indication = null, interactionSource = remember { MutableInteractionSource() })
        ) {
            FloconSurface(
                shape = FloconTheme.shapes.small,
                modifier = Modifier.fillMaxSize(0.9f)
            ) {
                entry.Content()
            }
            FloconIconTonalButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(
                            x = -16.dp.roundToPx(),
                            y = 16.dp.roundToPx()
                        )
                    }
            ) {
                FloconIcon(
                    imageVector = Icons.Sharp.Close
                )
            }
        }
    }
}

public class InnerWindowSceneStrategy<T : Any> : SceneStrategy<T> {

    public override fun SceneStrategyScope<T>.calculateScene(
        entries: List<NavEntry<T>>
    ): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null

        if (lastEntry.metadata[INNER_WINDOW_KEY] == true) {
            return InnerWindowScene(
                key = lastEntry.contentKey,
                entry = lastEntry,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                onBack = onBack,
            )
        }

        return null
    }

    public companion object {
        private val INNER_WINDOW_KEY = InnerWindowScene::class.qualifiedName!!

        public fun innerWindow(): Map<String, Any> = mapOf(INNER_WINDOW_KEY to true)
    }
}

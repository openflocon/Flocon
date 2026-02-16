@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.navigation.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.navigation.FloconRoute

@Immutable
private data class BigDialogScene(
    private val entry: NavEntry<FloconRoute>,
    override val previousEntries: List<NavEntry<FloconRoute>>,
    override val overlaidEntries: List<NavEntry<FloconRoute>>,
    private val onBack: () -> Unit,
) : OverlayScene<FloconRoute> {
    override val key: Any = BigDialogSceneStrategy.BIG_DIALOG
    override val entries: List<NavEntry<FloconRoute>> = listOf(entry)
    override val content: @Composable (() -> Unit) = {
        FloconScaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Title")
                    },
                    actions = {
                        FloconIconButton(
                            onClick = onBack
                        ) {
                            FloconIcon(
                                imageVector = Icons.Default.Close
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = FloconTheme.colorPalette.primary,
                        titleContentColor = FloconTheme.colorPalette.onPrimary,
                        actionIconContentColor = FloconTheme.colorPalette.onPrimary
                    )
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .background(color = FloconTheme.colorPalette.primary.copy(alpha = 0.5f))
                .clickable(
                    onClick = onBack,
                    indication = null,
                    interactionSource = null
                )
                .padding(64.dp)
                .dropShadow(
                    shape = RoundedCornerShape(12.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        color = FloconTheme.colorPalette.onAccent
                    )
                )
                .clip(RoundedCornerShape(12.dp))
                .background(FloconTheme.colorPalette.primary)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                entry.Content()
            }
        }
    }
}

class BigDialogSceneStrategy : SceneStrategy<FloconRoute> {

    override fun SceneStrategyScope<FloconRoute>.calculateScene(
        entries: List<NavEntry<FloconRoute>>
    ): Scene<FloconRoute>? {
        val lastEntry = entries.lastOrNull()
        val dialogProperties = lastEntry?.metadata?.get(BIG_DIALOG) as? Boolean
        return dialogProperties?.let { properties ->
            BigDialogScene(
                entry = lastEntry,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                onBack = onBack,
            )
        }
    }

    companion object {
        const val BIG_DIALOG = "BIG_DIALOG"

        fun bigDialog(): Map<String, Any> = mapOf(BIG_DIALOG to true)
    }

}

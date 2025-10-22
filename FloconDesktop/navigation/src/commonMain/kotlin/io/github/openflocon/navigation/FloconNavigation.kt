package io.github.openflocon.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

@Composable
fun <T : Any> FloconNavigation(
    navigationState: FloconNavigationState<T>,
    modifier: Modifier = Modifier,
    sceneStrategy: SceneStrategy<T> = SinglePaneSceneStrategy(),
    builder: EntryProviderBuilder<T>.() -> Unit
) {
    NavDisplay(
        backStack = navigationState.stack,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator()
        ),
        sceneStrategy = sceneStrategy,
        onBack = { navigationState.back(it) },
        entryProvider = entryProvider {
            builder()
        },
        modifier = modifier
    )
}

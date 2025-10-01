package io.github.openflocon.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.NavigationEventDispatcherOwner
import io.github.openflocon.navigation.scene.MenuSceneStrategy

@Composable
fun FloconNavigation(
    navigationState: FloconNavigationState,
    modifier: Modifier = Modifier,
    builder: EntryProviderBuilder<FloconRoute>.() -> Unit
) {
    val dispatcher = remember { NavigationEventDispatcher() }
    val parent = remember {
        object : NavigationEventDispatcherOwner {
            override val navigationEventDispatcher: NavigationEventDispatcher = dispatcher
        }
    }

    LocalNavigationEventDispatcherOwner.provides(parent)

    NavigationEventDispatcherOwner(parent = parent) {
        NavDisplay(
            backStack = navigationState.stack,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            popTransitionSpec = { fadeIn() togetherWith fadeOut() },
            predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator()
            ),
            sceneStrategy = MenuSceneStrategy()
                .then(SinglePaneSceneStrategy()),
            onBack = { navigationState.back(it) },
            entryProvider = entryProvider {
                entry<LoadingRoute> {
                    Box(Modifier)
                }
                builder()
            },
            modifier = modifier
        )
    }
}

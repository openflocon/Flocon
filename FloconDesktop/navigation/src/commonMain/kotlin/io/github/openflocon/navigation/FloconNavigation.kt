package io.github.openflocon.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.NavigationEventDispatcherOwner
import org.koin.compose.koinInject

@Composable
fun FloconNavigation(
    builder: EntryProviderBuilder<FloconRoute>.() -> Unit
) {
    val navigationState = koinInject<FloconNavigationState>()
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
            entryDecorators = listOf(
//                rememberSceneSetupNavEntryDecorator(),
//                rememberSavedStateNavEntryDecorator()
            ),
            onBack = { navigationState.back(it) },
            entryProvider = entryProvider {
                entry<LoadingRoute> {
                    Box(Modifier)
                }
                builder()
            }
        )
    }
}

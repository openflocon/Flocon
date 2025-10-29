package io.github.openflocon.flocondesktop.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import io.github.openflocon.flocondesktop.app.ui.buildLeftPanelState
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.LeftPanelView
import io.github.openflocon.flocondesktop.features.analytics.analyticsRoutes
import io.github.openflocon.flocondesktop.features.network.networkRoutes
import io.github.openflocon.flocondesktop.menu.MenuSceneStrategy
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppScreen() {
    val viewModel = koinViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        navigationState = viewModel.navigationState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: AppUiState,
    navigationState: MainFloconNavigationState,
    onAction: (AppAction) -> Unit
) {
    // TODO Redo
    val menuState by produceState(
        buildLeftPanelState(SubScreen.Network),
        uiState.contentState.current
    ) {
        value = buildLeftPanelState(uiState.contentState.current)
    }

    FloconNavigation(
        navigationState = navigationState,
        sceneStrategy = PanelSceneStrategy()
            .then(WindowSceneStrategy())
            .then(DialogSceneStrategy())
            .then(MenuSceneStrategy {
                LeftPanelView(
                    state = menuState,
                    expanded = it,
                    onClickItem = { menu -> onAction(AppAction.SelectMenu(menu.screen)) }
                )
            })
            .then(SinglePaneSceneStrategy()),
        modifier = Modifier
            .fillMaxSize()
            .background(FloconTheme.colorPalette.surface)
    ) {
        networkRoutes()
        analyticsRoutes()
    }
}

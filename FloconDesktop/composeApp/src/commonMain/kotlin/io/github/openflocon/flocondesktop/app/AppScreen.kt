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
import io.github.openflocon.flocondesktop.app.ui.settings.settingsRoutes
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.LeftPanelView
import io.github.openflocon.flocondesktop.app.ui.view.topbar.MainScreenTopBar
import io.github.openflocon.flocondesktop.features.analytics.analyticsRoutes
import io.github.openflocon.flocondesktop.features.dashboard.dashboardRoutes
import io.github.openflocon.flocondesktop.features.database.databaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.deeplinkRoutes
import io.github.openflocon.flocondesktop.features.files.filesRoutes
import io.github.openflocon.flocondesktop.features.images.imageRoutes
import io.github.openflocon.flocondesktop.features.network.networkRoutes
import io.github.openflocon.flocondesktop.features.sharedpreferences.sharedPreferencesRoutes
import io.github.openflocon.flocondesktop.features.table.tableRoutes
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
            .then(
                MenuSceneStrategy(
                    menuContent = {
                        LeftPanelView(
                            state = menuState,
                            expanded = it,
                            onClickItem = { menu -> onAction(AppAction.SelectMenu(menu.screen)) }
                        )
                    },
                    topBarContent = {
                        MainScreenTopBar(
                            devicesState = uiState.deviceState,
                            appsState = uiState.appState,
                            recordState = uiState.recordState,
                            deleteApp = { onAction(AppAction.DeleteApp(it)) },
                            deleteDevice = { onAction(AppAction.DeleteDevice(it)) },
                            onDeviceSelected = { onAction(AppAction.SelectDevice(it)) },
                            onAppSelected = { onAction(AppAction.SelectApp(it)) },
                            onRecordClicked = { onAction(AppAction.Record) },
                            onRestartClicked = { onAction(AppAction.Restart) },
                            onTakeScreenshotClicked = { onAction(AppAction.Screenshoot) }
                        )
                    }
                ))
            .then(SinglePaneSceneStrategy()),
        modifier = Modifier
            .fillMaxSize()
            .background(FloconTheme.colorPalette.surface)
    ) {
        analyticsRoutes()
        dashboardRoutes()
        databaseRoutes()
        deeplinkRoutes()
        filesRoutes()
        imageRoutes()
        networkRoutes(navigationState)
        sharedPreferencesRoutes()
        tableRoutes()
        settingsRoutes()
    }
}

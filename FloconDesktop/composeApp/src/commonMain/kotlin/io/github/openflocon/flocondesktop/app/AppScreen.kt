package io.github.openflocon.flocondesktop.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import io.github.openflocon.flocondesktop.app.ui.releasenotes.releaseNotesRoutes
import io.github.openflocon.flocondesktop.app.ui.settings.settingsRoutes
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.LeftPanel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.MainScreenTopBar
import io.github.openflocon.flocondesktop.app.version.VersionChecker
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import io.github.openflocon.flocondesktop.common.ui.featuredoc.LocalHasDevice
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackHost
import io.github.openflocon.flocondesktop.features.accessibility.accessibilityRoutes
import io.github.openflocon.flocondesktop.features.adbcommander.adbCommanderRoutes
import io.github.openflocon.flocondesktop.features.analytics.analyticsRoutes
import io.github.openflocon.flocondesktop.features.crashreporter.crashReporterRoutes
import io.github.openflocon.flocondesktop.features.dashboard.dashboardRoutes
import io.github.openflocon.flocondesktop.features.database.databaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.deeplinkRoutes
import io.github.openflocon.flocondesktop.features.featureflags.featureFlagsRoutes
import io.github.openflocon.flocondesktop.features.files.filesRoutes
import io.github.openflocon.flocondesktop.features.images.imageRoutes
import io.github.openflocon.flocondesktop.features.network.networkRoutes
import io.github.openflocon.flocondesktop.features.sharedpreferences.sharedPreferencesRoutes
import io.github.openflocon.flocondesktop.features.table.tableRoutes
import io.github.openflocon.flocondesktop.features.timeline.timelineRoutes
import io.github.openflocon.flocondesktop.features.webview.webViewRoutes
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.scene.BigDialogSceneStrategy
import io.github.openflocon.navigation.scene.DialogSceneStrategy
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import org.koin.compose.viewmodel.koinViewModel

/** Above the big dialogs of the navigation (drawn at the depth of their entry in the stack): messages stay readable. */
internal const val AboveDialogsZIndex = 1_000f

@Composable
fun AppScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val versionCheckerViewModel = koinViewModel<VersionCheckerViewModel>()
    val updateChip by versionCheckerViewModel.updateChip.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Box(modifier = modifier.fillMaxSize()) {
        Content(
            uiState = uiState,
            navigationState = viewModel.navigationState,
            onAction = viewModel::onAction,
            updateChip = updateChip,
            onUpdateChipClicked = { uriHandler.openUri(it.link) },
        )
        FeedbackHost(modifier = Modifier.zIndex(AboveDialogsZIndex))
        VersionChecker()
    }
}

@Composable
private fun Content(
    uiState: AppUiState,
    navigationState: MainFloconNavigationState,
    onAction: (AppAction) -> Unit,
    updateChip: VersionCheckerViewModel.UpdateChipUiModel?,
    onUpdateChipClicked: (VersionCheckerViewModel.UpdateChipUiModel) -> Unit,
) {
    val sceneStrategies = remember { floconSceneStrategies() }

    CompositionLocalProvider(
        LocalHasDevice provides uiState.hasConnectedDevice,
    ) {
        FloconNavigation(
            navigationState = navigationState,
            sceneStrategies = sceneStrategies,
            sceneDecoratorStrategies = listOf(
                MenuSceneStrategy(
                    menuContent = { expanded, onToggleExpanded ->
                        LeftPanel(
                            current = uiState.contentState.current,
                            state = uiState.menuState,
                            expanded = expanded,
                            onToggleExpanded = onToggleExpanded,
                            onClickItem = { menu -> onAction(AppAction.SelectMenu(menu.screen)) },
                            onTogglePin = { menu -> onAction(AppAction.TogglePin(menu.screen)) },
                            onToggleSection = { section -> onAction(AppAction.ToggleSection(section.id)) },
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
                            onOpenDeviceInNewWindow = { onAction(AppAction.OpenDeviceInNewWindow(it)) },
                            onAppSelected = { onAction(AppAction.SelectApp(it)) },
                            onRecordClicked = { onAction(AppAction.Record) },
                            onRestartClicked = { onAction(AppAction.Restart) },
                            onTakeScreenshotClicked = { onAction(AppAction.Screenshoot) },
                            onOpenFolderClicked = { onAction(AppAction.OpenFloconFolder) },
                            updateChip = updateChip,
                            onUpdateChipClicked = onUpdateChipClicked,
                        )
                    }
                )
            ),
            // no background here: the themed page background shows through the islands
            modifier = Modifier.fillMaxSize()
        ) {
            floconRoutes()
        }
    }
}

internal fun floconSceneStrategies(): List<SceneStrategy<FloconRoute>> = listOf(
    PanelSceneStrategy(),
    WindowSceneStrategy(),
    DialogSceneStrategy(),
    BigDialogSceneStrategy(),
    SinglePaneSceneStrategy(),
)

/** Every destination of the app, also used by the windows opened for a device: a row of the timeline can open any of them. */
internal fun EntryProviderScope<FloconRoute>.floconRoutes() {
    analyticsRoutes()
    featureFlagsRoutes()
    dashboardRoutes()
    databaseRoutes()
    deeplinkRoutes()
    adbCommanderRoutes()
    filesRoutes()
    imageRoutes()
    networkRoutes()
    sharedPreferencesRoutes()
    tableRoutes()
    settingsRoutes()
    releaseNotesRoutes()
    crashReporterRoutes()
    timelineRoutes()
    webViewRoutes()
    accessibilityRoutes()
}

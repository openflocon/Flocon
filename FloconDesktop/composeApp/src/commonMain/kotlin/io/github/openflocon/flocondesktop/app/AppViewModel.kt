package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.screenshot_saved_at
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceInstalledPluginsUseCase
import io.github.openflocon.domain.device.usecase.RestartAppUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.log.LogManager
import io.github.openflocon.domain.menu.usecase.ObserveMenuPreferencesUseCase
import io.github.openflocon.domain.menu.usecase.ToggleCollapsedMenuSectionUseCase
import io.github.openflocon.domain.menu.usecase.TogglePinnedMenuItemUseCase
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.domain.versions.usecase.ShouldShowReleaseNotesUseCase
import io.github.openflocon.flocondesktop.BuildConfig
import io.github.openflocon.flocondesktop.app.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.app.ui.delegates.MenuDelegate
import io.github.openflocon.flocondesktop.app.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.id
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.applyPreferences
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.buildMenu
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.startScreenCandidates
import io.github.openflocon.flocondesktop.app.ui.releasenotes.ReleaseNotesRoutes
import io.github.openflocon.flocondesktop.app.ui.settings.SettingsRoutes
import io.github.openflocon.flocondesktop.common.utils.OpenFile
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.accessibility.AccessibilityRoutes
import io.github.openflocon.flocondesktop.features.adbcommander.AdbCommanderRoutes
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.crashreporter.CrashReporterRoutes
import io.github.openflocon.flocondesktop.features.dashboard.DashboardRoutes
import io.github.openflocon.flocondesktop.features.database.DatabaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkRoutes
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagsRoutes
import io.github.openflocon.flocondesktop.features.files.FilesRoutes
import io.github.openflocon.flocondesktop.features.images.ImageRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesRoutes
import io.github.openflocon.flocondesktop.features.table.TableRoutes
import io.github.openflocon.flocondesktop.features.timeline.TimelineRoutes
import io.github.openflocon.flocondesktop.features.webview.WebViewRoutes
import io.github.openflocon.flocondesktop.messages.ui.McpServerDelegate
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import io.github.openflocon.flocondesktop.window.DeviceWindowsController
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import kotlin.time.Duration.Companion.milliseconds

internal class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    mcpServerDelegate: McpServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
    shouldShowReleaseNotesUseCase: ShouldShowReleaseNotesUseCase,
    val navigationState: MainFloconNavigationState,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
    private val devicesDelegate: DevicesDelegate,
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val restartAppUseCase: RestartAppUseCase,
    private val recordVideoDelegate: RecordVideoDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val logManager: LogManager,
    private val settingsRepository: SettingsRepository,
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    observeMenuPreferencesUseCase: ObserveMenuPreferencesUseCase,
    private val menuDelegate: MenuDelegate,
    menuNavigator: MenuNavigator,
    private val deviceWindowsController: DeviceWindowsController,
) : ViewModel(messagesServerDelegate, mcpServerDelegate) {

    private val contentState = MutableStateFlow(
        ContentUiState(
            current = SubScreen.Timeline
        )
    )

    private val menuState: StateFlow<MenuState> = menuDelegate.menuState()
        .stateInWhileSubscribed(menuDelegate.initialMenuState())

    val uiState = combine(
        contentState,
        menuState,
        devicesDelegate.devicesState,
        devicesDelegate.appsState,
        recordVideoDelegate.state
    ) { content, menu, devices, apps, record ->
        AppUiState(
            contentState = content,
            menuState = menu,
            deviceState = devices,
            appState = apps,
            recordState = record
        )
    }
        .stateInWhileSubscribed(
            AppUiState(
                contentState = contentState.value,
                menuState = menuState.value,
                deviceState = devicesDelegate.devicesState.value,
                appState = devicesDelegate.appsState.value,
                recordState = recordVideoDelegate.state.value
            )
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            // a feature can ask to display another screen of the menu (ex: from the timeline)
            menuNavigator.requests.collect { screen ->
                onSelectMenu(AppAction.SelectMenu(screen))
            }
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            // the screen chosen in the settings, if it is still displayed
            val preferences = observeMenuPreferencesUseCase().first()
            startScreenCandidates(preferences.hiddenItems)
                .firstOrNull { it.screen.id == preferences.startScreen }
                ?.let { onSelectMenu(AppAction.SelectMenu(it.screen)) }
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            // the selected item can be hidden when another app is selected: go to the first available one
            menuState.collect { menu ->
                val available =
                    menu.topItems + menu.pinnedItems + menu.sections.flatMap { it.items } + menu.bottomItems
                val current = contentState.value.current
                if (available.isNotEmpty() && available.none { it.screen == current }) {
                    onSelectMenu(AppAction.SelectMenu(available.first().screen))
                }
            }
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            if (shouldShowReleaseNotesUseCase(current = BuildConfig.APP_VERSION)) {
                withContext(dispatcherProvider.ui) {
                    navigationState.navigate(ReleaseNotesRoutes.Current(BuildConfig.APP_VERSION))
                }
            }
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            initAdbPathUseCase()
                .alsoFailure {
                    logManager.e(TAG, "ADB init failed", it, domain = LogDomain.Default)
                    initialSetupStateHolder.setRequiresInitialSetup()
                }
                .alsoSuccess {
                    logManager.d(TAG, "ADB init OK", domain = LogDomain.Default)
                }

            messagesServerDelegate.initialize()
            mcpServerDelegate.initialize()

            launch {
                while (isActive) {
                    // ensure we have the forward enabled
                    startAdbForwardUseCase()
                        .alsoFailure {
                            settingsRepository.setAdbForwardStatus(AdbForwardStatus.NOK)
                        }
                        .alsoSuccess {
                            settingsRepository.setAdbForwardStatus(AdbForwardStatus.OK)
                        }
                    delay(1_500.milliseconds)
                }
            }
        }
    }

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.SelectMenu -> onSelectMenu(action)

            is AppAction.TogglePin -> viewModelScope.launch(dispatcherProvider.viewModel) {
                menuDelegate.togglePin(action.menu)
            }

            is AppAction.ToggleSection -> viewModelScope.launch(dispatcherProvider.viewModel) {
                menuDelegate.toggleSection(action.sectionId)
            }

            is AppAction.DeleteApp -> deleteApp(action)

            is AppAction.DeleteDevice -> deleteDevice(action)

            AppAction.Record -> onRecord()

            AppAction.Restart -> onRestart()

            AppAction.Screenshoot -> onTakeScreenshot()

            AppAction.OpenFloconFolder -> onOpenFloconFolder()

            is AppAction.SelectApp -> onAppSelected(action)

            is AppAction.SelectDevice -> onDeviceSelected(action)

            is AppAction.OpenDeviceInNewWindow -> deviceWindowsController.open(
                deviceId = action.device.id,
                title = action.device.deviceName,
            )
        }
    }

    private fun onSelectMenu(action: AppAction.SelectMenu) {
        if (action.menu != SubScreen.Settings) {
            contentState.update { it.copy(current = action.menu) }
        }

        menuDelegate.navigateTo(action.menu)
    }

    private fun onDeviceSelected(action: AppAction.SelectDevice) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.select(action.device.id)
        }
    }

    private fun deleteDevice(action: AppAction.DeleteDevice) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.delete(action.device.id)
        }
    }

    private fun deleteApp(action: AppAction.DeleteApp) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.deleteApp(action.app.packageName)
        }
    }

    private fun onAppSelected(action: AppAction.SelectApp) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.selectApp(action.app.packageName)
        }
    }

    private fun onRecord() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            recordVideoDelegate.toggleRecording()
        }
    }

    private fun onRestart() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            restartAppUseCase()
        }
    }

    private fun onTakeScreenshot() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            takeScreenshotUseCase().fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(it.message ?: "Unknown error")
                },
                doOnSuccess = {
                    feedbackDisplayer.displayMessage(getString(Res.string.screenshot_saved_at, it))
                },
            )
        }
    }

    private fun onOpenFloconFolder() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val folder = getFloconDocumentPathUseCase()
            if (!folder.exists()) {
                folder.mkdirs()
            }
            OpenFile.openFileOnDesktop(folder.absolutePath).alsoFailure {
                feedbackDisplayer.displayMessage(it.message)
            }
        }
    }

    companion object {
        private const val TAG = "AppViewModel"
    }
}

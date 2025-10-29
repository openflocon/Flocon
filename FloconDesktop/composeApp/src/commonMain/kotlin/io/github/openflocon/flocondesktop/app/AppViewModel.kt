package io.github.openflocon.flocondesktop.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.RestartAppUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.settings.usecase.InitAdbPathUseCase
import io.github.openflocon.domain.settings.usecase.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.app.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.app.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.buildMenu
import io.github.openflocon.flocondesktop.app.ui.settings.SettingsRoutes
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsRoutes
import io.github.openflocon.flocondesktop.features.dashboard.DashboardRoutes
import io.github.openflocon.flocondesktop.features.database.DatabaseRoutes
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkRoutes
import io.github.openflocon.flocondesktop.features.files.FilesRoutes
import io.github.openflocon.flocondesktop.features.images.ImageRoutes
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesRoutes
import io.github.openflocon.flocondesktop.features.table.TableRoutes
import io.github.openflocon.flocondesktop.messages.ui.MessagesServerDelegate
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class AppViewModel(
    messagesServerDelegate: MessagesServerDelegate,
    initAdbPathUseCase: InitAdbPathUseCase,
    startAdbForwardUseCase: StartAdbForwardUseCase,
    val navigationState: MainFloconNavigationState,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val dispatcherProvider: DispatcherProvider,
    private val devicesDelegate: DevicesDelegate,
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val restartAppUseCase: RestartAppUseCase,
    private val recordVideoDelegate: RecordVideoDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel(messagesServerDelegate) {

    private val contentState = MutableStateFlow(
        ContentUiState(
            current = SubScreen.Network
        )
    )
    private val menuState = MutableStateFlow(
        buildMenu(SubScreen.Network)
    )

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
            initAdbPathUseCase().alsoFailure {
                initialSetupStateHolder.setRequiresInitialSetup()
            }

            messagesServerDelegate.initialize()

            launch {
                while (isActive) {
                    // ensure we have the forward enabled
                    startAdbForwardUseCase()
                    delay(1_500)
                }
            }
        }
    }

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.SelectMenu -> onSelectMenu(action)
            is AppAction.DeleteApp -> deleteApp(action)
            is AppAction.DeleteDevice -> deleteDevice(action)
            AppAction.Record -> onRecord()
            AppAction.Restart -> onRestart()
            AppAction.Screenshoot -> onTakeScreenshot()
            is AppAction.SelectApp -> onAppSelected(action)
            is AppAction.SelectDevice -> onDeviceSelected(action)
        }
    }

    private fun onSelectMenu(action: AppAction.SelectMenu) {
        contentState.update { it.copy(current = action.menu) }
        navigationState.menu(
            when (action.menu) {
                SubScreen.Analytics -> AnalyticsRoutes.Main
                SubScreen.Dashboard -> DashboardRoutes.Main
                SubScreen.Database -> DatabaseRoutes.Main
                SubScreen.Deeplinks -> DeeplinkRoutes.Main
                SubScreen.Files -> FilesRoutes.Main
                SubScreen.Images -> ImageRoutes.Main
                SubScreen.Network -> NetworkRoutes.Main
                SubScreen.Settings -> SettingsRoutes.Main
                SubScreen.SharedPreferences -> SharedPreferencesRoutes.Main
                SubScreen.Tables -> TableRoutes.Main
            }
        )
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
                    feedbackDisplayer.displayMessage("Success, file saved at $it")
                },
            )
        }
    }

}

package io.github.openflocon.flocondesktop.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.device.models.DeviceCapabilitiesDomainModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceCapabilitiesUseCase
import io.github.openflocon.domain.device.usecase.RestartAppUseCase
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.menu.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.menu.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.menu.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.menu.ui.view.displayName
import io.github.openflocon.flocondesktop.menu.ui.view.icon
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MainViewModel(
    private val devicesDelegate: DevicesDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val takeScreenshotUseCase: TakeScreenshotUseCase,
    private val restartAppUseCase: RestartAppUseCase,
    private val recordVideoDelegate: RecordVideoDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeCurrentDeviceCapabilitiesUseCase: ObserveCurrentDeviceCapabilitiesUseCase,
    private val mainNavigationState: MainFloconNavigationState
) : ViewModel(
    devicesDelegate,
    recordVideoDelegate,
), KoinComponent {

    // TODO Remove
    val subScreen = MutableStateFlow<SubScreen>(SubScreen.Network)

    val recordState: StateFlow<RecordVideoStateUiModel> = recordVideoDelegate.state

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            initialSetupStateHolder.needsAdbSetup.collect {
                if (it) {
                    subScreen.update { SubScreen.Settings }
                }
            }
        }
    }

    val leftPanelState = combines(
        subScreen,
        observeCurrentDeviceCapabilitiesUseCase(),
    ).map { (subScreen, capabilities) ->
        buildLeftPanelState(
            current = subScreen,
        )
    }
        .flowOn(dispatcherProvider.ui)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = buildLeftPanelState(subScreen.value),
        )

    val devicesState: StateFlow<DevicesStateUiModel> = devicesDelegate.devicesState
    val appsState: StateFlow<AppsStateUiModel> = devicesDelegate.appsState


    fun onDeviceSelected(device: DeviceItemUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.select(device.id)
        }
    }

    fun deleteDevice(device: DeviceItemUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.delete(device.id)
        }
    }

    fun deleteApp(app: DeviceAppUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.deleteApp(app.packageName)
        }
    }

    fun onAppSelected(app: DeviceAppUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.selectApp(app.packageName)
        }
    }

    fun onClickLeftPanelItem(leftPanelItem: LeftPanelItem) {
        menuNavigationState.navigate(leftPanelItem.screen)
    }

    fun onRecordClicked() {
        // TODO Needs scoped vm
//        mainNavigationState.navigate(MenuRoutes.App("zfjzhefoezf"))
        viewModelScope.launch(dispatcherProvider.viewModel) {
            recordVideoDelegate.toggleRecording()
        }
    }

    fun onRestartClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            restartAppUseCase()
        }
    }

    fun onTakeScreenshotClicked() {
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

internal fun buildLeftPanelState(current: SubScreen) = LeftPanelState(
    current = current,
    bottomItems = listOf(
        item(subScreen = SubScreen.Settings)
    ),
    sections = listOf(
        LeftPannelSection(
            title = "Network",
            items = listOf(
                item(subScreen = SubScreen.Network),
                item(subScreen = SubScreen.Images),
            ),
        ),
        LeftPannelSection(
            title = "Storage",
            items = listOf(
                item(SubScreen.Database),
                item(SubScreen.SharedPreferences),
                item(SubScreen.Files),
            ),
        ),
        LeftPannelSection(
            title = "Data",
            items = listOf(
                item(SubScreen.Dashboard),
                item(SubScreen.Analytics),
                item(SubScreen.Tables),
            ),
        ),
        LeftPannelSection(
            title = "Actions",
            items = listOf(
                item(SubScreen.Deeplinks)
            ),
        ),
    ),
)

private fun item(
    subScreen: SubScreen
): LeftPanelItem {
    return LeftPanelItem(
        screen = subScreen,
        icon = subScreen.icon(),
        text = subScreen.displayName()
    )
}

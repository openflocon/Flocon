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
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.menu.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.menu.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.menu.ui.view.displayName
import io.github.openflocon.flocondesktop.menu.ui.view.icon
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MenuViewModel(
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

    private val leftPanelState = MutableStateFlow(buildLeftPanelState(current = SubScreen.Network))

    val uiState = combine(
        leftPanelState,
        recordVideoDelegate.state,
        devicesDelegate.devicesState,
        devicesDelegate.appsState
    ) { content, record, device, apps ->
        MenuUiState(
            leftPanelState = content,
            recordVideoState = record,
            appsStateUiModel = apps,
            devicesStateUiModel = device
        )
    }
        .stateInWhileSubscribed(
            MenuUiState(
                leftPanelState = leftPanelState.value,
                recordVideoState = recordVideoDelegate.state.value,
                devicesStateUiModel = devicesDelegate.devicesState.value,
                appsStateUiModel = devicesDelegate.appsState.value
            )
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            initialSetupStateHolder.needsAdbSetup.collect {
                if (it) {
                    menuNavigationState.navigate(SubScreen.Settings)
                    leftPanelState.update { state -> state.copy(current = SubScreen.Settings) }
                }
            }
        }
    }

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
        leftPanelState.update { state -> state.copy(current = leftPanelItem.screen) }
        menuNavigationState.navigate(leftPanelItem.screen)
    }

    fun onRecordClicked() {
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

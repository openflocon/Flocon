package io.github.openflocon.flocondesktop.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.TakeScreenshotUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.main.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.main.ui.delegates.RecordVideoDelegate
import io.github.openflocon.flocondesktop.main.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.main.ui.model.id
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.main.ui.view.displayName
import io.github.openflocon.flocondesktop.main.ui.view.icon
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val devicesDelegate: DevicesDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val takeScreenshotUseCase : TakeScreenshotUseCase,
    private val recordVideoDelegate: RecordVideoDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel(
    devicesDelegate,
    recordVideoDelegate,
) {
    val subScreen = MutableStateFlow(SubScreen.Network)

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

    val leftPanelState = subScreen.map { subScreen ->
        buildLeftPanelState(
            selectedId = subScreen.id,
        )
    }.flowOn(dispatcherProvider.ui)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = buildLeftPanelState(subScreen.value.id),
        )

    val devicesState: StateFlow<DevicesStateUiModel> = devicesDelegate.devicesState
    val appsState: StateFlow<AppsStateUiModel> = devicesDelegate.appsState

    fun onDeviceSelected(device: DeviceItemUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.select(device.id)
        }
    }

    fun onAppSelected(app: DeviceAppUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            devicesDelegate.selectApp(app.packageName)
        }
    }

    fun onClickLeftPanelItem(leftPanelItem: LeftPanelItem) {
        this.subScreen.update { SubScreen.fromId(leftPanelItem.id) }
    }

    fun onRecordClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            recordVideoDelegate.toggleRecording()
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

fun buildLeftPanelState(selectedId: String?) = LeftPanelState(
    bottomItems = listOf(
        item(subScreen = SubScreen.Settings, selectedId = selectedId),
    ),
    sections = listOf(
        LeftPannelSection(
            title = "Network",
            items = listOf(
                item(subScreen = SubScreen.Network, selectedId = selectedId),
                item(subScreen = SubScreen.Images, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Storage",
            items = listOf(
                item(SubScreen.Database, selectedId = selectedId),
                item(SubScreen.SharedPreferences, selectedId = selectedId),
                item(SubScreen.Files, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Data",
            items = listOf(
                item(SubScreen.Dashboard, selectedId = selectedId),
                item(SubScreen.Analytics, selectedId = selectedId),
                item(SubScreen.Tables, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Actions",
            items = listOf(
                item(SubScreen.Deeplinks, selectedId = selectedId),
            ),
        ),
    ),
)

private fun item(
    subScreen: SubScreen,
    selectedId: String?,
): LeftPanelItem {
    val id = subScreen.id
    return LeftPanelItem(
        id = id,
        icon = subScreen.icon(),
        text = subScreen.displayName(),
        isSelected = selectedId == id,
    )
}

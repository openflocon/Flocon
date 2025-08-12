package io.github.openflocon.flocondesktop.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.domain.dashboard.usecase.ObserveCurrentDeviceDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.SendCheckBoxUpdateDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SendClickEventToDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SubmitTextFieldToDeviceDeviceUseCase
import io.github.openflocon.flocondesktop.features.dashboard.delegate.DashboardSelectorDelegate
import io.github.openflocon.flocondesktop.features.dashboard.mapper.toUi
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val observeCurrentDeviceDashboardUseCase: ObserveCurrentDeviceDashboardUseCase,
    private val sendClickEventToDeviceDeviceUseCase: SendClickEventToDeviceDeviceUseCase,
    private val submitTextFieldToDeviceDeviceUseCase: SubmitTextFieldToDeviceDeviceUseCase,
    private val sendCheckBoxUpdateDeviceDeviceUseCase: SendCheckBoxUpdateDeviceDeviceUseCase,
    private val dashboardSelectorDelegate: DashboardSelectorDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel(dashboardSelectorDelegate) {

    val deviceDashboards: StateFlow<DashboardsStateUiModel> = dashboardSelectorDelegate.deviceDashboards

    val state: StateFlow<DashboardViewState?> =
        observeCurrentDeviceDashboardUseCase()
            .map { it?.toUi() }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null,
            )

    fun onVisible() {
        // databaseSelectorDelegate.start()
    }

    fun onNotVisible() {
        // databaseSelectorDelegate.stop()
    }

    fun onButtonClicked(buttonId: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            sendClickEventToDeviceDeviceUseCase(buttonId)
        }
    }

    fun onTextFieldSubmit(textFieldId: String, value: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            submitTextFieldToDeviceDeviceUseCase(textFieldId = textFieldId, value = value)
        }
    }

    fun onUpdateCheckBox(checkBoxId: String, value: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            sendCheckBoxUpdateDeviceDeviceUseCase(checkBoxId = checkBoxId, value = value)
        }
    }

    fun onDashboardSelected(selected: DeviceDashboardUiModel) {
        dashboardSelectorDelegate.onDashboardSelected(selected.id)
    }
}

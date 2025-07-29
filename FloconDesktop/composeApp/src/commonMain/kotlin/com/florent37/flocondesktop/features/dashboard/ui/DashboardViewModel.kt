package com.florent37.flocondesktop.features.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.common.ui.feedback.FeedbackDisplayer
import com.florent37.flocondesktop.features.dashboard.domain.ObserveCurrentDeviceDashboardUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SendCheckBoxUpdateDeviceDeviceUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SendClickEventToDeviceDeviceUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SubmitTextFieldToDeviceDeviceUseCase
import com.florent37.flocondesktop.features.dashboard.ui.delegate.DashboardSelectorDelegate
import com.florent37.flocondesktop.features.dashboard.ui.mapper.toUi
import com.florent37.flocondesktop.features.dashboard.ui.model.DashboardViewState
import com.florent37.flocondesktop.features.dashboard.ui.model.DashboardsStateUiModel
import com.florent37.flocondesktop.features.dashboard.ui.model.DeviceDashboardUiModel
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

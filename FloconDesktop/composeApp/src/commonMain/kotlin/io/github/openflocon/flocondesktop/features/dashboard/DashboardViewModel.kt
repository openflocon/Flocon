package io.github.openflocon.flocondesktop.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.dashboard_removed
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.usecase.DeleteCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.DeleteDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveCurrentDeviceDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveDashboardArrangementUseCase
import io.github.openflocon.domain.dashboard.usecase.SelectDashboardArrangementUseCase
import io.github.openflocon.domain.dashboard.usecase.SendCheckBoxUpdateDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SendClickEventToDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SubmitFormToDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SubmitTextFieldToDeviceDeviceUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.dashboard.delegate.DashboardSelectorDelegate
import io.github.openflocon.flocondesktop.features.dashboard.mapper.toUi
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardArrangement
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardViewState
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardsStateUiModel
import io.github.openflocon.flocondesktop.features.dashboard.model.DeviceDashboardUiModel
import io.github.openflocon.flocondesktop.features.network.list.delegate.OpenBodyDelegate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class DashboardViewModel(
    private val observeCurrentDeviceDashboardUseCase: ObserveCurrentDeviceDashboardUseCase,
    private val sendClickEventToDeviceDeviceUseCase: SendClickEventToDeviceDeviceUseCase,
    private val submitFormToDeviceDeviceUseCase: SubmitFormToDeviceDeviceUseCase,
    private val submitTextFieldToDeviceDeviceUseCase: SubmitTextFieldToDeviceDeviceUseCase,
    private val sendCheckBoxUpdateDeviceDeviceUseCase: SendCheckBoxUpdateDeviceDeviceUseCase,
    private val dashboardSelectorDelegate: DashboardSelectorDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val deleteCurrentDeviceSelectedDashboardUseCase: DeleteCurrentDeviceSelectedDashboardUseCase,
    private val deleteDashboardUseCase: DeleteDashboardUseCase,
    private val observeDashboardArrangementUseCase: ObserveDashboardArrangementUseCase,
    private val selectDashboardArrangementUseCase: SelectDashboardArrangementUseCase,
    private val openBodyDelegate: OpenBodyDelegate,
) : ViewModel(dashboardSelectorDelegate) {

    val deviceDashboards: StateFlow<DashboardsStateUiModel> =
        dashboardSelectorDelegate.deviceDashboards

    val arrangement: StateFlow<DashboardArrangement> =
        observeDashboardArrangementUseCase()
            .map { it.toUi() }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                DashboardArrangement.Adaptive,
            )

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

    fun onFormSubmit(formId: String, values: Map<String, Any>) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            submitFormToDeviceDeviceUseCase(formId, values)
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

    fun deleteCurrentDashboard() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteCurrentDeviceSelectedDashboardUseCase()
            feedbackDisplayer.displayMessage(getString(Res.string.dashboard_removed))
        }
    }

    fun onDeleteClicked(dashboard: DeviceDashboardUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteDashboardUseCase(dashboard.id)
            feedbackDisplayer.displayMessage(getString(Res.string.dashboard_removed))
        }
    }

    fun onArrangementClicked(arrangement: DashboardArrangement) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            selectDashboardArrangementUseCase(
                when (arrangement) {
                    is DashboardArrangement.Adaptive -> DashboardArrangementDomainModel.Adaptive
                    is DashboardArrangement.Fixed ->
                        DashboardArrangementDomainModel.Fixed(
                            itemsPerRow = arrangement.itemsPerRow
                        )
                }
            )
        }
    }

    fun onOpenExternalClicked(content: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            openBodyDelegate.openBodyExternally(content)
                .alsoFailure {
                    feedbackDisplayer.displayMessage(it.message)
                }
        }
    }
}

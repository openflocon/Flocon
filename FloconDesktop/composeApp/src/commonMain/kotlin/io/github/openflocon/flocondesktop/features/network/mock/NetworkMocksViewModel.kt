package io.github.openflocon.flocondesktop.features.network.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.models.MockDeviceTarget
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.DeleteNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.GenerateNetworkMockFromNetworkCallUseCase
import io.github.openflocon.domain.network.usecase.mocks.GetNetworkMockByIdUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMockIsEnabledUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMocksDeviceUseCase
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockEditionWindowUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.mapper.toLineUi
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMockAction
import io.github.openflocon.flocondesktop.features.network.mock.processor.ExportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.mock.processor.ExportResult
import io.github.openflocon.flocondesktop.features.network.mock.processor.ImportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.mock.processor.ImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkMocksViewModel(
    private val observeNetworkMocksUseCase: ObserveNetworkMocksUseCase,
    private val getNetworkMock: GetNetworkMockByIdUseCase,
    private val generateNetworkMockFromNetworkCall: GenerateNetworkMockFromNetworkCallUseCase,
    private val updateNetworkMockIsEnabledUseCase: UpdateNetworkMockIsEnabledUseCase,
    private val updateNetworkMocksDeviceUseCase: UpdateNetworkMocksDeviceUseCase,
    private val addNetworkMocksUseCase: AddNetworkMocksUseCase,
    private val deleteNetworkMocksUseCase: DeleteNetworkMocksUseCase,
    private val exportMocksProcessor: ExportMocksProcessor,
    private val importMocksProcessor: ImportMocksProcessor,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val items = observeNetworkMocksUseCase()
        .distinctUntilChanged()
        .map { it.map { it.toLineUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val editionWindow = MutableStateFlow<MockEditionWindowUiModel?>(null)

    fun initWith(fromNetworkCallId: String?) {
        fromNetworkCallId?.let { callId ->
            viewModelScope.launch(dispatcherProvider.viewModel) {
                generateNetworkMockFromNetworkCall(callId)
                    ?.let { openEdition(it) }
            }
        }
    }

    fun onAction(action: NetworkMockAction)  {
        when(action) {
            is NetworkMockAction.ChangeIsEnabled -> changeIsEnabled(
                id = action.id,
                enabled = action.enabled
            )
            is NetworkMockAction.ChangeIsShared -> changeIsShared(
                id = action.id,
                isShared = action.isShared
            )
            is NetworkMockAction.OnAddItemClicked -> createNewMock()
            is NetworkMockAction.OnDeleteClicked -> deleteMock(id = action.id)
            is NetworkMockAction.OnItemClicked -> clickOnMock(id = action.id)
            is NetworkMockAction.OnExportClicked -> exportMocks()
            is NetworkMockAction.OnImportClicked -> importMocks()
        }
    }

    private fun deleteMock(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteNetworkMocksUseCase(id)
        }
    }

    private fun exportMocks() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when(val result = exportMocksProcessor()) {
                is ExportResult.Cancelled -> { /* no op */ }
                is ExportResult.Failure -> feedbackDisplayer.displayMessage(result.error.message ?: "error")
                is ExportResult.Success -> feedbackDisplayer.displayMessage("Export successful")
            }
        }
    }

    private fun importMocks() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when(val result = importMocksProcessor()) {
                is ImportResult.Cancelled -> { /* no op */ }
                is ImportResult.Failure -> feedbackDisplayer.displayMessage(result.error.message ?: "error")
                is ImportResult.Success -> {
                    result.mocks.forEach {
                        addNetworkMocksUseCase(it)
                    }
                    feedbackDisplayer.displayMessage("Import successful")
                }
            }
        }
    }

    private fun changeIsEnabled(id: String, enabled: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            updateNetworkMockIsEnabledUseCase(id = id, isEnabled = enabled)
        }
    }

    private fun changeIsShared(id: String, isShared: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            updateNetworkMocksDeviceUseCase(id = id, when(isShared){
                true -> MockDeviceTarget.AllDevicesAndApps
                false -> MockDeviceTarget.SpecificToCurrentDeviceAndApp
            })
        }
    }

    private fun openEdition(item: MockNetworkDomainModel) {
        val mock = item.toUi()
        editionWindow.update {
            MockEditionWindowUiModel(
                SelectedMockUiModel.Edition(mock),
            )
        }
    }

    private fun clickOnMock(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            getNetworkMock(id)?.let { openEdition(it) }
        }
    }

    private fun createNewMock() {
        editionWindow.update {
            MockEditionWindowUiModel(
                SelectedMockUiModel.Creation,
            )
        }
    }

    fun cancelMockCreation() {
        editionWindow.update {
            null
        }
    }

    fun addMock(uiModel: MockNetworkUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            addNetworkMocksUseCase(uiModel.toDomain())
            // close
            editionWindow.update {
                null
            }
            feedbackDisplayer.displayMessage("Saved")
        }
    }
}

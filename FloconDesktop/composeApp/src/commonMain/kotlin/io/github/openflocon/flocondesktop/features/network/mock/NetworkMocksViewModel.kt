package io.github.openflocon.flocondesktop.features.network.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.models.MockDeviceTarget
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.DeleteNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMockIsEnabledUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMocksDeviceUseCase
import io.github.openflocon.flocondesktop.features.network.NetworkRoutes
import io.github.openflocon.flocondesktop.features.network.mock.list.mapper.toLineUi
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMockAction
import io.github.openflocon.flocondesktop.features.network.mock.processor.ExportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.mock.processor.ExportResult
import io.github.openflocon.flocondesktop.features.network.mock.processor.ImportMocksProcessor
import io.github.openflocon.flocondesktop.features.network.mock.processor.ImportResult
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetworkMocksViewModel(
    private val observeNetworkMocksUseCase: ObserveNetworkMocksUseCase,
    private val updateNetworkMockIsEnabledUseCase: UpdateNetworkMockIsEnabledUseCase,
    private val updateNetworkMocksDeviceUseCase: UpdateNetworkMocksDeviceUseCase,
    private val addNetworkMocksUseCase: AddNetworkMocksUseCase,
    private val deleteNetworkMocksUseCase: DeleteNetworkMocksUseCase,
    private val exportMocksProcessor: ExportMocksProcessor,
    private val importMocksProcessor: ImportMocksProcessor,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val navigationState: MainFloconNavigationState,
) : ViewModel() {

    val items = observeNetworkMocksUseCase()
        .distinctUntilChanged()
        .map { it.map { it.toLineUi() }.toImmutableList() }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf(),
        )

    private var initialized = false

    /** Opens the edition of a mock generated from a network call, once: the list stays under the dialog. */
    fun initWith(fromNetworkCallId: String?) {
        if (initialized) return
        initialized = true
        fromNetworkCallId?.let { callId ->
            navigationState.navigate(NetworkRoutes.MockEdition(fromNetworkCallId = callId))
        }
    }

    fun onAction(action: NetworkMockAction) {
        when (action) {
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
            when (val result = exportMocksProcessor()) {
                is ExportResult.Cancelled -> { /* no op */ }
                is ExportResult.Failure -> feedbackDisplayer.displayMessage(result.error.message ?: "error")
                is ExportResult.Success -> feedbackDisplayer.displayMessage("Export successful")
            }
        }
    }

    private fun importMocks() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (val result = importMocksProcessor()) {
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
            updateNetworkMocksDeviceUseCase(
                id = id,
                when (isShared) {
                    true -> MockDeviceTarget.AllDevicesAndApps
                    false -> MockDeviceTarget.SpecificToCurrentDeviceAndApp
                }
            )
        }
    }

    private fun clickOnMock(id: String) {
        navigationState.navigate(NetworkRoutes.MockEdition(mockId = id))
    }

    private fun createNewMock() {
        navigationState.navigate(NetworkRoutes.MockEdition())
    }
}

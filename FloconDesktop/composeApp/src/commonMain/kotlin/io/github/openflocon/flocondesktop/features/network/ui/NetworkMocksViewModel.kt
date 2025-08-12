package io.github.openflocon.flocondesktop.features.network.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.DeleteNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.GetNetworkMockByIdUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toLineUi
import io.github.openflocon.flocondesktop.features.network.ui.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.SelectedMockUiModel
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
    private val addNetworkMocksUseCase: AddNetworkMocksUseCase,
    private val deleteNetworkMocksUseCase: DeleteNetworkMocksUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val items = observeNetworkMocksUseCase()
        .distinctUntilChanged()
        .map { it.map { toLineUi(it) } }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val selectedItem = MutableStateFlow<SelectedMockUiModel?>(null)

    fun deleteMock(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteNetworkMocksUseCase(id)
        }
    }

    fun clickOnMock(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            getNetworkMock(id)?.let { toUi(it) }?.let { mock ->
                selectedItem.update {
                    SelectedMockUiModel.Edition(mock)
                }
            }
        }
    }

    fun createNewMock() {
        selectedItem.update {
            SelectedMockUiModel.Creation
        }
    }

    fun cancelMockCreation() {
        selectedItem.update {
            null
        }
    }

    fun addMock(uiModel: MockNetworkUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            addNetworkMocksUseCase(toDomain(uiModel))
            // close
            selectedItem.update {
                null
            }
        }
    }

}

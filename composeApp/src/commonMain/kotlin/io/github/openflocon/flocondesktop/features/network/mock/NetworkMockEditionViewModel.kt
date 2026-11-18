package io.github.openflocon.flocondesktop.features.network.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.saved
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.GenerateNetworkMockFromNetworkCallUseCase
import io.github.openflocon.domain.network.usecase.mocks.GetNetworkMockByIdUseCase
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * What the mock edition is about: both null is a new mock, [mockId] is an existing one, [fromNetworkCallId] a mock
 * generated from a network call. A single parameter, as Koin resolves the parameters by type.
 */
data class MockEditionTarget(
    val mockId: String?,
    val fromNetworkCallId: String?,
)

class NetworkMockEditionViewModel(
    private val target: MockEditionTarget,
    private val getNetworkMock: GetNetworkMockByIdUseCase,
    private val generateNetworkMockFromNetworkCall: GenerateNetworkMockFromNetworkCallUseCase,
    private val addNetworkMocksUseCase: AddNetworkMocksUseCase,
    private val navigationState: MainFloconNavigationState,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    private val _state = MutableStateFlow<SelectedMockUiModel?>(null)

    /** Null while the mock is loading */
    val state: StateFlow<SelectedMockUiModel?> = _state.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) { load() }
    }

    private suspend fun load() {
        val mock = when {
            target.mockId != null -> getNetworkMock(target.mockId)

            target.fromNetworkCallId != null -> generateNetworkMockFromNetworkCall(target.fromNetworkCallId)

            else -> {
                _state.value = SelectedMockUiModel.Creation
                return
            }
        }
        if (mock == null) {
            // the mock or the call does not exist (anymore): nothing to edit
            navigationState.back()
        } else {
            _state.value = SelectedMockUiModel.Edition(mock.toUi())
        }
    }

    fun save(mock: MockNetworkUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            addNetworkMocksUseCase(mock.toDomain())
            feedbackDisplayer.displayMessage(getString(Res.string.saved))
            navigationState.back()
        }
    }

    fun cancel() {
        navigationState.back()
    }
}

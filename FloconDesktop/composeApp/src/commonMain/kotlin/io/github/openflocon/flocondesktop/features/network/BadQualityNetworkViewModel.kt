package io.github.openflocon.flocondesktop.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.usecase.badquality.ObserveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SaveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.UpdateNetworkBadQualityIsEnabledUseCase
import io.github.openflocon.domain.network.usecase.mocks.AddNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.DeleteNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.GenerateNetworkMockFromNetworkCallUseCase
import io.github.openflocon.domain.network.usecase.mocks.GetNetworkMockByIdUseCase
import io.github.openflocon.domain.network.usecase.mocks.ObserveNetworkMocksUseCase
import io.github.openflocon.domain.network.usecase.mocks.UpdateNetworkMockIsEnabledUseCase
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.network.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.mapper.toLineUi
import io.github.openflocon.flocondesktop.features.network.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.model.badquality.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.model.mocks.MockEditionWindowUiModel
import io.github.openflocon.flocondesktop.features.network.model.mocks.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.model.mocks.SelectedMockUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BadQualityNetworkViewModel(
    private val observeNetworkBadQualityUseCase: ObserveNetworkBadQualityUseCase,
    private val saveNetworkBadQualityUseCase: SaveNetworkBadQualityUseCase,
    private val updateNetworkBadQualityIsEnabledUseCase: UpdateNetworkBadQualityIsEnabledUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    val viewState = observeNetworkBadQualityUseCase()
        .distinctUntilChanged()
        .map { toUi(it) }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun changeIsEnabled(enabled: Boolean) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            updateNetworkBadQualityIsEnabledUseCase(isEnabled = enabled)
        }
    }

    fun save(uiModel: BadQualityConfigUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveNetworkBadQualityUseCase(toDomain(uiModel))
            // close
            feedbackDisplayer.displayMessage("Saved")
        }
    }
}

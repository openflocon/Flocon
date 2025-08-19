package io.github.openflocon.flocondesktop.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.usecase.badquality.ObserveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SaveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.UpdateNetworkBadQualityIsEnabledUseCase
import io.github.openflocon.flocondesktop.features.network.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.model.badquality.BadQualityConfigUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BadQualityNetworkViewModel(
    private val observeNetworkBadQualityUseCase: ObserveNetworkBadQualityUseCase,
    private val saveNetworkBadQualityUseCase: SaveNetworkBadQualityUseCase,
    private val updateNetworkBadQualityIsEnabledUseCase: UpdateNetworkBadQualityIsEnabledUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    enum class Event {
        Close
    }

    private val _events = Channel<BadQualityNetworkViewModel.Event?>()
    val events: Flow<Event?> = _events.receiveAsFlow()

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
            _events.send(Event.Close)
            feedbackDisplayer.displayMessage("Saved")
        }
    }
}

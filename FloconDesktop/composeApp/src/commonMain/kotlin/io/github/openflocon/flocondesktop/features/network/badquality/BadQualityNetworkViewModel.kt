package io.github.openflocon.flocondesktop.features.network.badquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.network.models.BadQualityConfigId
import io.github.openflocon.domain.network.usecase.badquality.DeleteBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveAllNetworkBadQualitiesUseCase
import io.github.openflocon.domain.network.usecase.badquality.ObserveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SaveNetworkBadQualityUseCase
import io.github.openflocon.domain.network.usecase.badquality.SetNetworkBadQualityEnabledConfigUseCase
import io.github.openflocon.flocondesktop.features.network.badquality.edition.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.badquality.edition.mapper.toUi
import io.github.openflocon.flocondesktop.features.network.badquality.list.mapper.toLineUi
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BadQualityNetworkViewModel(
    private val observeAllNetworkBadQualitiesUseCase: ObserveAllNetworkBadQualitiesUseCase,
    private val observeNetworkBadQualityUseCase: ObserveNetworkBadQualityUseCase,
    private val deleteBadQualityUseCase: DeleteBadQualityUseCase,
    private val saveNetworkBadQualityUseCase: SaveNetworkBadQualityUseCase,
    private val setNetworkBadQualityEnabledConfigUseCase: SetNetworkBadQualityEnabledConfigUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    enum class Event {
        Close
    }

    private val _events = Channel<BadQualityNetworkViewModel.Event?>()
    val events: Flow<Event?> = _events.receiveAsFlow()

    val items = observeAllNetworkBadQualitiesUseCase()
        .distinctUntilChanged()
        .map { it.map { it.toLineUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val selectedItem = MutableStateFlow<SelectedBadQualityUiModel?>(null)

    fun setEnabledElement(configId: BadQualityConfigId?) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            setNetworkBadQualityEnabledConfigUseCase(configId = configId)
        }
    }

    fun delete(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteBadQualityUseCase(id)
        }
    }

    fun select(id: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            // TODO get
            val existing = observeNetworkBadQualityUseCase(id).firstOrNull()?.toUi()
            selectedItem.value = existing?.let {
                SelectedBadQualityUiModel.Edition(
                    config = existing
                )
            } ?: SelectedBadQualityUiModel.Creation
        }
    }

    fun create() {
        selectedItem.value = SelectedBadQualityUiModel.Creation
    }

    fun save(uiModel: BadQualityConfigUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            saveNetworkBadQualityUseCase(uiModel.toDomain())
            // close
            selectedItem.value = null
            feedbackDisplayer.displayMessage("Saved")
        }
    }

    fun closeEdition() {
        selectedItem.value = null
    }
}

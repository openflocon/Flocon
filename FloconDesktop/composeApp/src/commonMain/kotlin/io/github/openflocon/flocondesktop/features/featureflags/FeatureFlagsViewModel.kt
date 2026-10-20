package io.github.openflocon.flocondesktop.features.featureflags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagsUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.featureflags.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.library.designsystem.common.asState
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview

class FeatureFlagsViewModel(
    private val dispatcherProvider: DispatcherProvider,
    observeCurrentDeviceFeatureFlagsUseCase: ObserveCurrentDeviceFeatureFlagsUseCase,
    private val navigationState: MainFloconNavigationState,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText.asState()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val rows: StateFlow<ImmutableList<FeatureFlagRowUiModel>> = snapshotFlow { _filterText.value }
        .debounce(150)
        .flatMapLatest { filter ->
            observeCurrentDeviceFeatureFlagsUseCase(
                filter = filter.takeIf { it.isNotBlank() },
            )
        }
        .map { list -> list.map { it.mapToUi() }.toImmutableList() }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(persistentListOf())

    fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    fun onAction(action: FeatureFlagsAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is FeatureFlagsAction.OnClick -> navigationState.navigate(
                    FeatureFlagsRoutes.Detail(
                        provider = action.item.provider,
                        name = action.item.name,
                    )
                )
            }
        }
    }
}

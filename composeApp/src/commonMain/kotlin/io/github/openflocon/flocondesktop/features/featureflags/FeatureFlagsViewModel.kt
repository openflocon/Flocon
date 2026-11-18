package io.github.openflocon.flocondesktop.features.featureflags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagProvidersUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceFeatureFlagsUseCase
import io.github.openflocon.domain.featureflags.usecase.ObserveCurrentDeviceSelectedFeatureFlagProviderUseCase
import io.github.openflocon.domain.featureflags.usecase.SelectCurrentDeviceFeatureFlagProviderUseCase
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.flocondesktop.features.featureflags.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagProvidersUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagVariantFilterUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.library.designsystem.common.asState
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeatureFlagsViewModel(
    private val dispatcherProvider: DispatcherProvider,
    observeCurrentDeviceFeatureFlagsUseCase: ObserveCurrentDeviceFeatureFlagsUseCase,
    observeCurrentDeviceFeatureFlagProvidersUseCase: ObserveCurrentDeviceFeatureFlagProvidersUseCase,
    observeCurrentDeviceSelectedFeatureFlagProviderUseCase: ObserveCurrentDeviceSelectedFeatureFlagProviderUseCase,
    private val selectCurrentDeviceFeatureFlagProviderUseCase: SelectCurrentDeviceFeatureFlagProviderUseCase,
    private val navigationState: MainFloconNavigationState,
) : ViewModel() {

    private val _filterText = mutableStateOf("")
    val filterText: State<String> = _filterText.asState()

    val providersState: StateFlow<FeatureFlagProvidersUiModel> = combine(
        observeCurrentDeviceFeatureFlagProvidersUseCase(),
        observeCurrentDeviceSelectedFeatureFlagProviderUseCase(),
    ) { providers, selected ->
        if (providers.isEmpty()) {
            FeatureFlagProvidersUiModel.Empty
        } else {
            FeatureFlagProvidersUiModel.WithContent(
                providers = providers,
                // the selected provider may not exist anymore (new app session)
                selected = selected?.takeIf { it in providers } ?: providers.first().also {
                    selectCurrentDeviceFeatureFlagProviderUseCase(it)
                },
            )
        }
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(FeatureFlagProvidersUiModel.Loading)

    // variants the user unchecked: new variants show up checked
    private val _hiddenVariants = MutableStateFlow(emptySet<String>())
    val hiddenVariants: StateFlow<Set<String>> = _hiddenVariants.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val allRows: StateFlow<List<FeatureFlagRowUiModel>> = snapshotFlow { _filterText.value }
        .debounce(150)
        .flatMapLatest { filter ->
            observeCurrentDeviceFeatureFlagsUseCase(
                filter = filter.takeIf { it.isNotBlank() },
            )
        }
        .map { list -> list.map { it.mapToUi() } }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(emptyList())

    // on, off, then the custom variants by name
    val variants: StateFlow<ImmutableList<FeatureFlagVariantFilterUiModel>> = allRows
        .map { list ->
            list.groupBy { it.variant }
                .map { (variant, flags) -> FeatureFlagVariantFilterUiModel(variant, flags.first().state, flags.size) }
                .sortedWith(compareBy({ it.state }, { it.variant.lowercase() }))
                .toImmutableList()
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(persistentListOf())

    val rows: StateFlow<ImmutableList<FeatureFlagRowUiModel>> = combine(allRows, _hiddenVariants) { list, hidden ->
        list.filter { it.variant !in hidden }.toImmutableList()
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateInWhileSubscribed(persistentListOf())

    val numberOfFlags: StateFlow<Int?> = rows.map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun onVariantToggled(variant: String) {
        _hiddenVariants.update { if (variant in it) it - variant else it + variant }
    }

    fun onFilterTextChanged(value: String) {
        _filterText.value = value
    }

    fun onProviderSelected(provider: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceFeatureFlagProviderUseCase(provider)
        }
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

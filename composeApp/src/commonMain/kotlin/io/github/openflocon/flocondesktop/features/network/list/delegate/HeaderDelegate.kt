package io.github.openflocon.flocondesktop.features.network.list.delegate

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.usecase.GetNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.ObserveNetworkFilterUseCase
import io.github.openflocon.domain.network.usecase.UpdateNetworkFilterUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.network.list.mapper.toDomain
import io.github.openflocon.flocondesktop.features.network.list.mapper.toTextFilterDomain
import io.github.openflocon.flocondesktop.features.network.list.mapper.toTextFilterUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.NetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.NetworkStatusColumnUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.NetworkTextColumnUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.MethodFilterState
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HeaderDelegate(
    private val closeableDelegate: CloseableDelegate,
    private val observeNetworkFilterUseCase: ObserveNetworkFilterUseCase,
    private val updateNetworkFilterUseCase: UpdateNetworkFilterUseCase,
    private val getNetworkFilterUseCase: GetNetworkFilterUseCase,
) : CloseableScoped by closeableDelegate {

    data class HeaderQuery(
        val sort: NetworkSortDomainModel?,
        val textFilters: List<NetworkFilterDomainModel.Filters>,
        val methods: List<String>?,
    )

    data class Sorted(
        val column: NetworkColumnsTypeUiModel,
        val sort: SortedByUiModel.Enabled,
    )

    private val textFiltersState: StateFlow<Map<NetworkTextFilterColumns, TextFilterStateUiModel>> =
        observeNetworkFilterUseCase()
            .map { it.mapValues { (key, value) -> toTextFilterUi(value) } }
            .stateIn(coroutineScope, started = SharingStarted.WhileSubscribed(5_000), emptyMap())

    private val sorted = MutableStateFlow<Sorted?>(null)

    private val methodFilterState = MutableStateFlow(
        MethodFilterState(
            items = NetworkMethodUi.all().map {
                MethodFilterState.Item(
                    method = it,
                    isSelected = true,
                )
            },
            isEnabled = true,
        ),
    )

    val headerState: StateFlow<NetworkHeaderUiState> = coroutineScope.launchMolecule(
        RecompositionMode.Immediate
    ) {
        val sorted by sorted.collectAsState()
        val methodFilterState by methodFilterState.collectAsState()
        val textFiltersState by textFiltersState.collectAsState()

        buildNetworkColumnsValue(
            sorted = sorted,
            methodFilterState = methodFilterState,
            textFiltersState = textFiltersState,
        )
    }

    /** Header inputs expressed as domain values, used to query the network list. */
    val queryInputs: StateFlow<HeaderQuery> = coroutineScope.launchMolecule(
        RecompositionMode.Immediate
    ) {
        val sorted by sorted.collectAsState()
        val methodFilterState by methodFilterState.collectAsState()
        val textFiltersState by textFiltersState.collectAsState()

        HeaderQuery(
            sort = sorted.toDomain(),
            textFilters = remember(textFiltersState) { textFiltersState.toDomain() },
            methods = remember(methodFilterState) {
                methodsToDomain(
                    methodFilterState.items
                        .filter(MethodFilterState.Item::isSelected)
                        .map(MethodFilterState.Item::method)
                )
            },
        )
    }

    private fun buildNetworkColumnsValue(
        sorted: Sorted?,
        methodFilterState: MethodFilterState,
        textFiltersState: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    ): NetworkHeaderUiState = NetworkHeaderUiState(
        requestTime = NetworkTextColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.RequestTime }?.sort
                ?: SortedByUiModel.None,
            filter = textFiltersState[NetworkTextFilterColumns.RequestTime]
                ?: TextFilterStateUiModel.EMPTY,
        ),
        method = NetworkMethodColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Method }?.sort
                ?: SortedByUiModel.None,
            filter = methodFilterState,
        ),
        domain = NetworkTextColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Domain }?.sort
                ?: SortedByUiModel.None,
            filter = textFiltersState[NetworkTextFilterColumns.Domain]
                ?: TextFilterStateUiModel.EMPTY,
        ),
        query = NetworkTextColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Query }?.sort
                ?: SortedByUiModel.None,
            filter = textFiltersState[NetworkTextFilterColumns.Query]
                ?: TextFilterStateUiModel.EMPTY,
        ),
        status = NetworkStatusColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Status }?.sort
                ?: SortedByUiModel.None,
            filter = textFiltersState[NetworkTextFilterColumns.Status]
                ?: TextFilterStateUiModel.EMPTY,
        ),
        time = NetworkTextColumnUiModel(
            sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Time }?.sort
                ?: SortedByUiModel.None,
            filter = textFiltersState[NetworkTextFilterColumns.Time]
                ?: TextFilterStateUiModel.EMPTY,
        ),
    )

    fun onClickSort(type: NetworkColumnsTypeUiModel, sort: SortedByUiModel.Enabled) {
        val newValue = Sorted(
            column = type,
            sort = sort,
        )
        sorted.update {
            // click again to remove
            if (it == newValue) {
                null
            } else {
                newValue
            }
        }
    }

    fun onFilterAction(action: OnFilterAction) {
        coroutineScope.launch {
            when (action) {
                is OnFilterAction.ClickOnMethod -> {
                    val clicked = action.methodUi
                    methodFilterState.update {
                        it.copy(
                            items = it.items.map { item ->
                                if (item.method == clicked) {
                                    item.copy(isSelected = !item.isSelected)
                                } else {
                                    item
                                }
                            },
                        )
                    }
                }

                is OnFilterAction.TextFilter -> {
                    textFilterAction(column = action.column, action = action.action)
                }
            }
        }
    }

    private suspend fun textFilterAction(
        column: NetworkTextFilterColumns,
        action: TextFilterAction,
    ) {
        val filter: TextFilterStateUiModel =
            getNetworkFilterUseCase(column).let { toTextFilterUi(it) }
        val updated = filter.performAction(action)

        updateNetworkFilterUseCase(
            column = column,
            newValue = toTextFilterDomain(updated),
        )
    }

    private fun methodsToDomain(items: List<NetworkMethodUi>): List<String>? = items.map(NetworkMethodUi::text)
        .takeIf { it.isNotEmpty() }
        ?.takeIf { it.size != NetworkMethodUi.all().size }
}

private fun TextFilterStateUiModel.performAction(
    action: TextFilterAction,
): TextFilterStateUiModel = when (action) {
    is TextFilterAction.Delete -> {
        copy(
            includedFilters = includedFilters - action.item,
            excludedFilters = excludedFilters - action.item,
        )
    }

    is TextFilterAction.Exclude -> {
        val newItem = TextFilterStateUiModel.FilterItem(
            text = action.text,
            isActive = true,
            isExcluded = true,
        )
        val exists = excludedFilters.any { it.text == newItem.text }
        if (exists) {
            copy(
                excludedFilters = (
                    excludedFilters.map {
                        if (it.text == action.text) {
                            newItem
                        } else {
                            it
                        }
                    }
                    ).distinctBy { it.text }
            )
        } else {
            copy(excludedFilters = (excludedFilters + newItem).distinctBy { it.text })
        }
    }

    is TextFilterAction.Include -> {
        val newItem = TextFilterStateUiModel.FilterItem(
            text = action.text,
            isActive = true,
            isExcluded = false,
        )
        val exists = includedFilters.any { it.text == newItem.text }
        if (exists) {
            copy(
                includedFilters = (
                    includedFilters.map {
                        if (it.text == action.text) {
                            newItem
                        } else {
                            it
                        }
                    }
                    ).distinctBy { it.text }
            )
        } else {
            copy(includedFilters = (includedFilters + newItem).distinctBy { it.text })
        }
    }

    is TextFilterAction.SetIsActive -> {
        this.copy(
            includedFilters = includedFilters.map {
                if (it == action.item) {
                    action.item.copy(isActive = action.isActive)
                } else {
                    it
                }
            },
            excludedFilters = excludedFilters.map {
                if (it == action.item) {
                    action.item.copy(isActive = action.isActive)
                } else {
                    it
                }
            },
        )
    }
}

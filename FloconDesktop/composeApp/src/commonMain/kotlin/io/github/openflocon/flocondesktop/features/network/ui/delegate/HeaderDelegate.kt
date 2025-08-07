package io.github.openflocon.flocondesktop.features.network.ui.delegate

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.OnFilterAction
import io.github.openflocon.flocondesktop.features.network.ui.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkStatusColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkTextColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.MethodFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.StatusFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterColumns
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HeaderDelegate(
    private val closeableDelegate: CloseableDelegate,
    dispatcherProvider: DispatcherProvider,
) : CloseableScoped by closeableDelegate {

    data class Sorted(
        val column: NetworkColumnsTypeUiModel,
        val sort: SortedByUiModel.Enabled,
    )

    val textFiltersState = MutableStateFlow<Map<TextFilterColumns, TextFilterState>>(emptyMap())

    val sorted = MutableStateFlow<Sorted?>(null)
    private val methodFilterState = MutableStateFlow<MethodFilterState>(
        MethodFilterState(
            items = NetworkMethodUi.all().map {
                MethodFilterState.Item(
                    method = it,
                    isSelected = true,
                )
            },
            isEnabled = true,
        )
    )

    fun allowedMethods() = methodFilterState.map {
        it.items
            .filter { item -> item.isSelected }
            .map { item -> item.method }
    }.distinctUntilChanged()

    val headerUiState = combine(
        sorted,
        methodFilterState,
        textFiltersState,
    ) { sorted, methodFilterState, textFiltersState ->
        buildHeaderValue(
            sorted = sorted,
            methodFilterState = methodFilterState,
            textFiltersState = textFiltersState,
        )
    }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            defaultHeaderValue()
        )

    fun buildHeaderValue(
        sorted: Sorted?,
        methodFilterState: MethodFilterState,
        textFiltersState: Map<TextFilterColumns, TextFilterState>,
    ): NetworkHeaderUiState {
        return NetworkHeaderUiState(
            requestTime = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.RequestTime }?.sort
                    ?: SortedByUiModel.None,
                filter = textFiltersState[TextFilterColumns.RequestTime] ?: TextFilterState.EMPTY,
            ),
            method = NetworkMethodColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Method }?.sort
                    ?: SortedByUiModel.None,
                filter = methodFilterState,
            ),
            domain = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Domain }?.sort
                    ?: SortedByUiModel.None,
                filter = textFiltersState[TextFilterColumns.Domain] ?: TextFilterState.EMPTY,
            ),
            query = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Query }?.sort
                    ?: SortedByUiModel.None,
                filter = textFiltersState[TextFilterColumns.Query] ?: TextFilterState.EMPTY,
            ),
            status = NetworkStatusColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Status }?.sort
                    ?: SortedByUiModel.None,
                filter = StatusFilterState(isEnabled = false), // TODO
            ),
            time = NetworkTextColumnUiModel(
                sortedBy = sorted?.takeIf { it.column == NetworkColumnsTypeUiModel.Time }?.sort
                    ?: SortedByUiModel.None,
                filter = textFiltersState[TextFilterColumns.Time] ?: TextFilterState.EMPTY,
            ),
        )
    }

    fun defaultHeaderValue(): NetworkHeaderUiState {
        return NetworkHeaderUiState(
            requestTime = NetworkTextColumnUiModel.EMPTY,
            method = NetworkMethodColumnUiModel.EMPTY,
            domain = NetworkTextColumnUiModel.EMPTY,
            query = NetworkTextColumnUiModel.EMPTY,
            status = NetworkStatusColumnUiModel.EMPTY,
            time = NetworkTextColumnUiModel.EMPTY,
        )
    }

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
        when (action) {
            is OnFilterAction.ClickOnMethod -> {
                val clicked = action.methodUi
                methodFilterState.update {
                    it.copy(items = it.items.map { item ->
                        if (item.method == clicked) {
                            item.copy(isSelected = !item.isSelected)
                        } else item
                    })
                }
            }

            is OnFilterAction.TextFilter -> {
                textFilterAction(column = action.column, action = action.action)
            }
        }
    }

    private fun textFilterAction(column: TextFilterColumns, action: TextFilterAction) {
        val filter = textFiltersState.value[column] ?: TextFilterState(
            includedFilters = listOf(),
            excludedFilters = listOf(),
            isEnabled = true,
        )
        val updated = parformAction(filter, action)

        textFiltersState.update {
            it + Pair(column, updated)
        }
    }
}

private fun parformAction(
    filter: TextFilterState,
    action: TextFilterAction
): TextFilterState {
    return when (action) {
        is TextFilterAction.Delete -> {
            filter.copy(
                includedFilters = filter.includedFilters - action.item,
                excludedFilters = filter.excludedFilters - action.item,
            )
        }

        is TextFilterAction.Exclude -> {
            filter.copy(
                excludedFilters = (filter.excludedFilters + TextFilterState.FilterItem(
                    text = action.text,
                    isActive = true,
                    isExcluded = true
                )).distinctBy { it.text },
            )
        }

        is TextFilterAction.Include -> {
            filter.copy(
                includedFilters = (filter.includedFilters + TextFilterState.FilterItem(
                    text = action.text,
                    isActive = true,
                    isExcluded = false
                )).distinctBy { it.text },
            )
        }

        is TextFilterAction.SetIsActive -> {
            filter.copy(
                includedFilters = filter.includedFilters.map {
                    if (it == action.item) {
                        action.item.copy(isActive = action.isActive)
                    } else it
                },
                excludedFilters = filter.excludedFilters.map {
                    if (it == action.item) {
                        action.item.copy(isActive = action.isActive)
                    } else it
                },
            )
        }
    }
}

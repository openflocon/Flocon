package io.github.openflocon.flocondesktop.features.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableContentUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemsBeforeUseCase
import io.github.openflocon.domain.table.usecase.ResetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.flocondesktop.features.network.list.mapper.formatTimestamp
import io.github.openflocon.flocondesktop.features.table.delegate.TableSelectorDelegate
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableAction
import io.github.openflocon.flocondesktop.features.table.model.TableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TableViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val tableSelectorDelegate: TableSelectorDelegate,
    private val observeCurrentDeviceTableContentUseCase: ObserveCurrentDeviceTableContentUseCase,
    private val resetCurrentDeviceSelectedTableUseCase: ResetCurrentDeviceSelectedTableUseCase,
    private val removeTableItemUseCase: RemoveTableItemUseCase,
    private val removeTableItemsBeforeUseCase: RemoveTableItemsBeforeUseCase,
) : ViewModel() {
    val deviceTables: StateFlow<TablesStateUiModel> = tableSelectorDelegate.deviceTables

    private val _selectedItem = MutableStateFlow<TableRowUiModel?>(null)
    val selectedItem = _selectedItem.asStateFlow()

    val content: StateFlow<TableContentStateUiModel> =
        observeCurrentDeviceTableContentUseCase()
            .map {
                if (it == null || it.items.isEmpty()) {
                    TableContentStateUiModel.Empty
                } else {
                    TableContentStateUiModel.WithContent(
                        rows = it.items.map { item ->
                            TableRowUiModel(
                                id = item.itemId,
                                values = buildList {
                                    add(formatTimestamp(item.createdAt))
                                    addAll(item.values)
                                },
                                columns = buildList {
                                    add("time")
                                    addAll(item.columns)
                                },
                            )
                        },
                    )
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TableContentStateUiModel.Loading,
            )

    fun onVisible() {
        // no op
    }

    fun onNotVisible() {
        // no op
    }

    fun onTableSelected(selected: DeviceTableUiModel) {
        tableSelectorDelegate.onTableSelected(selected)
    }

    fun onResetClicked() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            resetCurrentDeviceSelectedTableUseCase()
        }
    }

    fun onTableAction(action: TableAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is TableAction.OnClick -> {
                    _selectedItem.update {
                        if (it == action.item) {
                            null
                        } else {
                            action.item
                        }
                    }
                }
                is TableAction.Remove -> removeTableItemUseCase(action.item.id)
                is TableAction.RemoveLinesAbove -> removeTableItemsBeforeUseCase(action.item.id)
                TableAction.ClosePanel -> {
                    _selectedItem.update { null }
                }
            }
        }
    }
}

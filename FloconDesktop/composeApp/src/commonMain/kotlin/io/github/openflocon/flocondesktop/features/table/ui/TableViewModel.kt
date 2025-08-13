package io.github.openflocon.flocondesktop.features.table.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableContentUseCase
import io.github.openflocon.domain.table.usecase.ResetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.network.mapper.formatTimestamp
import io.github.openflocon.flocondesktop.features.table.ui.delegate.TableSelectorDelegate
import io.github.openflocon.flocondesktop.features.table.ui.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.ui.model.TableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.ui.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.ui.model.TablesStateUiModel
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

    fun onClickItem(item: TableRowUiModel?) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            _selectedItem.update {
                if (it == item) {
                    null
                } else {
                    item
                }
            }
        }
    }
}

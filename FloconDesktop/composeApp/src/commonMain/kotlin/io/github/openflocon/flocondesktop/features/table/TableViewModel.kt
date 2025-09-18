package io.github.openflocon.flocondesktop.features.table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.table.usecase.ExportTableToCsvUseCase
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceTableContentUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemsBeforeUseCase
import io.github.openflocon.domain.table.usecase.ResetCurrentDeviceSelectedTableUseCase
import io.github.openflocon.flocondesktop.features.network.list.mapper.formatTimestamp
import io.github.openflocon.flocondesktop.features.table.delegate.TableSelectorDelegate
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableAction
import io.github.openflocon.flocondesktop.features.table.model.TableColumnsUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableContentStateUiModel
import io.github.openflocon.flocondesktop.features.table.model.TableRowUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val tableSelectorDelegate: TableSelectorDelegate,
    private val observeCurrentDeviceTableContentUseCase: ObserveCurrentDeviceTableContentUseCase,
    private val resetCurrentDeviceSelectedTableUseCase: ResetCurrentDeviceSelectedTableUseCase,
    private val removeTableItemUseCase: RemoveTableItemUseCase,
    private val removeTableItemsBeforeUseCase: RemoveTableItemsBeforeUseCase,
    private val exportTableToCsv: ExportTableToCsvUseCase,
) : ViewModel() {
    val deviceTables: StateFlow<TablesStateUiModel> = tableSelectorDelegate.deviceTables

    val content: StateFlow<TableContentStateUiModel> =
        observeCurrentDeviceTableContentUseCase()
            .map {
                if (it == null || it.items.isEmpty()) {
                    TableContentStateUiModel.Empty
                } else {
                    TableContentStateUiModel.WithContent(
                        columns = TableColumnsUiModel(buildList {
                            add("time")
                            addAll(it.columns)
                        }),
                        rows = it.items.map { item ->
                            TableRowUiModel(
                                id = item.itemId,
                                values = buildList {
                                    add(formatTimestamp(item.createdAt))
                                    addAll(item.values)
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

    fun onAction(action: TableAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is TableAction.Remove -> removeTableItemUseCase(action.item.id)
                is TableAction.RemoveLinesAbove -> removeTableItemsBeforeUseCase(action.item.id)
                is TableAction.ExportCsv -> onExportCsv()
            }
        }
    }

    private fun onExportCsv() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            exportTableToCsv().fold(
                doOnFailure = {
                    feedbackDisplayer.displayMessage(
                        "Error while exporting csv"
                    )
                },
                doOnSuccess = { path ->
                    feedbackDisplayer.displayMessage(
                        "Csv exported at $path"
                    )
                }
            )
        }
    }

}

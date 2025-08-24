package io.github.openflocon.flocondesktop.features.table.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.table.usecase.ObserveCurrentDeviceSelectedTableUseCase
import io.github.openflocon.domain.table.usecase.ObserveDeviceTablesUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemUseCase
import io.github.openflocon.domain.table.usecase.RemoveTableItemsBeforeUseCase
import io.github.openflocon.domain.table.usecase.SelectCurrentDeviceTableUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.table.mapper.toUi
import io.github.openflocon.flocondesktop.features.table.model.DeviceTableUiModel
import io.github.openflocon.flocondesktop.features.table.model.TablesStateUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableSelectorDelegate(
    observeDeviceTableUseCase: ObserveDeviceTablesUseCase,
    observeCurrentDeviceSelectedTableUseCase: ObserveCurrentDeviceSelectedTableUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val selectCurrentDeviceTableUseCase: SelectCurrentDeviceTableUseCase,
    private val removeTableItemsBeforeUseCase: RemoveTableItemsBeforeUseCase,
    private val removeTableItemUseCase: RemoveTableItemUseCase,
) : CloseableScoped by closeableDelegate {

    val deviceTables: StateFlow<TablesStateUiModel> =
        combine(
            observeDeviceTableUseCase(),
            observeCurrentDeviceSelectedTableUseCase(),
        ) { tables, selected ->
            if (tables.isEmpty()) {
                TablesStateUiModel.Empty
            } else {
                TablesStateUiModel.WithContent(
                    tables = tables.map { it.toUi() },
                    selected =
                        (selected ?: run {
                            tables.first().also {
                                selectCurrentDeviceTableUseCase(it.id)
                            }
                        }).toUi(),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                TablesStateUiModel.Loading,
            )

    fun onTableSelected(table: DeviceTableUiModel) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceTableUseCase(table.id)
        }
    }
}

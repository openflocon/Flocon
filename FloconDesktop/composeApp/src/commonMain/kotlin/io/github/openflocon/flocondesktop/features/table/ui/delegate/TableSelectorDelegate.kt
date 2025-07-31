package io.github.openflocon.flocondesktop.features.table.ui.delegate

import com.florent37.flocondesktop.common.coroutines.closeable.CloseableDelegate
import com.florent37.flocondesktop.common.coroutines.closeable.CloseableScoped
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.table.domain.ObserveCurrentDeviceSelectedTableUseCase
import com.florent37.flocondesktop.features.table.domain.ObserveDeviceTablesUseCase
import com.florent37.flocondesktop.features.table.domain.SelectCurrentDeviceTableUseCase
import com.florent37.flocondesktop.features.table.domain.model.TableIdentifierDomainModel
import com.florent37.flocondesktop.features.table.ui.model.DeviceTableUiModel
import com.florent37.flocondesktop.features.table.ui.model.TablesStateUiModel
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
                    tables = tables.map { toUi(it) },
                    selected =
                    toUi(
                        selected ?: run {
                            tables.first().also {
                                selectCurrentDeviceTableUseCase(it.id)
                            }
                        },
                    ),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.Companion.WhileSubscribed(5_000),
                TablesStateUiModel.Loading,
            )

    fun toUi(table: TableIdentifierDomainModel) = DeviceTableUiModel(
        id = table.id,
        name = table.name,
    )

    fun onTableSelected(table: DeviceTableUiModel) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceTableUseCase(table.id)
        }
    }
}

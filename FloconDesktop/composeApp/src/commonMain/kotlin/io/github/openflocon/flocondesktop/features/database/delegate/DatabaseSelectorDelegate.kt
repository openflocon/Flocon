package io.github.openflocon.flocondesktop.features.database.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.usecase.AskForDeviceDatabasesUseCase
import io.github.openflocon.domain.database.usecase.GetDeviceDatabaseTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveDeviceDatabaseUseCase
import io.github.openflocon.domain.database.usecase.SelectCurrentDeviceDatabaseUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DatabaseSelectorDelegate(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val observeDeviceDatabaseUseCase: ObserveDeviceDatabaseUseCase,
    private val observeCurrentDeviceSelectedDatabaseAndTablesUseCase: ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val askForDeviceDatabasesUseCase: AskForDeviceDatabasesUseCase,
    private val selectCurrentDeviceDatabaseUseCase: SelectCurrentDeviceDatabaseUseCase,
    private val getDeviceDatabaseTablesUseCase: GetDeviceDatabaseTablesUseCase,
) : CloseableScoped by closeableDelegate {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> =
        combine(
            observeDeviceDatabaseUseCase(),
            observeCurrentDeviceSelectedDatabaseAndTablesUseCase(),
        ) { databases, selected ->
            if (databases.isEmpty()) {
                DatabasesStateUiModel.Empty
            } else {
                val selected = selected ?: DatabaseAndTablesDomainModel(
                    database = databases.first(),
                    tables = emptyList(),
                )
                DatabasesStateUiModel.WithContent(
                    databases = databases.map {
                        it.toUi(
                            selected = selected,
                        )
                    },
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                DatabasesStateUiModel.Loading,
            )

    fun onDatabaseSelected(databaseId: DeviceDataBaseId) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceDatabaseUseCase(databaseId)
            getDeviceDatabaseTablesUseCase()
        }
    }

    private var askForDeviceDatabasesJob: Job? = null

    fun start() {
        askForDeviceDatabasesJob =
            coroutineScope.launch(dispatcherProvider.viewModel) {
                // if we change the device, we should ask again
                observeCurrentDeviceIdUseCase()
                    .distinctUntilChanged()
                    .collect {
                        askForDeviceDatabasesUseCase()
                    }
            }
    }

    fun stop() {
        askForDeviceDatabasesJob?.cancel()
    }
}

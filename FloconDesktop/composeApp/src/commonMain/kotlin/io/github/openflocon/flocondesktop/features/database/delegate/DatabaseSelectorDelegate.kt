package io.github.openflocon.flocondesktop.features.database.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.usecase.AskForDeviceDatabasesUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.domain.database.usecase.ObserveDeviceDatabaseUseCase
import io.github.openflocon.domain.database.usecase.SelectCurrentDeviceDatabaseUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
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
    private val observeCurrentDeviceSelectedDatabaseUseCase: ObserveCurrentDeviceSelectedDatabaseUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val askForDeviceDatabasesUseCase: AskForDeviceDatabasesUseCase,
    private val selectCurrentDeviceDatabaseUseCase: SelectCurrentDeviceDatabaseUseCase,
) : CloseableScoped by closeableDelegate {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> =
        combine(
            observeDeviceDatabaseUseCase(),
            observeCurrentDeviceSelectedDatabaseUseCase(),
        ) { databases, selected ->
            if (databases.isEmpty()) {
                DatabasesStateUiModel.Empty
            } else {
                DatabasesStateUiModel.WithContent(
                    databases = databases.map { toUi(it) },
                    selected = toUi(selected ?: databases.first()),
                )
            }
        }.flowOn(dispatcherProvider.viewModel)
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                DatabasesStateUiModel.Loading,
            )

    fun toUi(database: DeviceDataBaseDomainModel) = DeviceDataBaseUiModel(
        id = database.id,
        name = database.name,
    )

    fun onDatabaseSelected(databaseId: DeviceDataBaseId) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceDatabaseUseCase(databaseId)
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

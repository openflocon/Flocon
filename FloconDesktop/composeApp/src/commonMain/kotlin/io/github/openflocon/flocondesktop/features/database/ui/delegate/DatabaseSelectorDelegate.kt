package io.github.openflocon.flocondesktop.features.database.ui.delegate

import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceUseCase
import io.github.openflocon.domain.database.usecase.AskForDeviceDatabasesUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseUseCase
import io.github.openflocon.domain.database.usecase.ObserveDeviceDatabaseUseCase
import io.github.openflocon.domain.database.usecase.SelectCurrentDeviceDatabaseUseCase
import io.github.openflocon.domain.models.DeviceDataBaseDomainModel
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.ui.model.DeviceDataBaseUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DatabaseSelectorDelegate(
    private val observeCurrentDeviceUseCase: ObserveCurrentDeviceUseCase,
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
                SharingStarted.Companion.WhileSubscribed(5_000),
                DatabasesStateUiModel.Loading,
            )

    fun toUi(database: DeviceDataBaseDomainModel) = DeviceDataBaseUiModel(
        id = database.id,
        name = database.name,
    )

    fun onDatabaseSelected(database: DeviceDataBaseUiModel) {
        coroutineScope.launch(dispatcherProvider.viewModel) {
            selectCurrentDeviceDatabaseUseCase(database.id)
        }
    }

    private var askForDeviceDatabasesJob: Job? = null

    fun start() {
        askForDeviceDatabasesJob =
            coroutineScope.launch(dispatcherProvider.viewModel) {
                // if we change the device, we should ask again
                observeCurrentDeviceUseCase()
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

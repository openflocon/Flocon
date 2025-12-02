package io.github.openflocon.flocondesktop.features.database.delegate

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DatabaseAndTablesDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.usecase.AskForDeviceDatabasesUseCase
import io.github.openflocon.domain.database.usecase.GetDatabaseByIdUseCase
import io.github.openflocon.domain.database.usecase.GetDeviceDatabaseTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase
import io.github.openflocon.domain.database.usecase.ObserveDeviceDatabaseUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseSelectorDelegate(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val observeDeviceDatabaseUseCase: ObserveDeviceDatabaseUseCase,
    private val getDatabaseByIdUseCase: GetDatabaseByIdUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val askForDeviceDatabasesUseCase: AskForDeviceDatabasesUseCase,
    private val getDeviceDatabaseTablesUseCase: GetDeviceDatabaseTablesUseCase,
    private val observeCurrentDeviceSelectedDatabaseAndTablesUseCase: ObserveCurrentDeviceSelectedDatabaseAndTablesUseCase,
) : CloseableScoped by closeableDelegate {

    val selectedDatabaseId = MutableStateFlow<DeviceDataBaseDomainModel?>(null)

    val deviceDataBases: StateFlow<DatabasesStateUiModel> =
        combine(
            observeDeviceDatabaseUseCase(),
            selectedDatabaseId.flatMapLatest {
                if (it == null) flowOf(null)
                else observeCurrentDeviceSelectedDatabaseAndTablesUseCase(it)
            },
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

    suspend fun onDatabaseSelected(databaseId: DeviceDataBaseId): DeviceDataBaseDomainModel? = getDatabaseByIdUseCase(databaseId)?.also { database ->
        selectedDatabaseId.update { database }
        getDeviceDatabaseTablesUseCase(databaseId)
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

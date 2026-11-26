package io.github.openflocon.flocondesktop.features.database

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DatabaseRowUpdateResultDomainModel
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.database.usecase.GetRowEditTargetUseCase
import io.github.openflocon.domain.database.usecase.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.domain.database.usecase.UpdateDatabaseRowUseCase
import io.github.openflocon.domain.database.usecase.favorite.GetFavoriteQueryByIdDatabaseUseCase
import io.github.openflocon.domain.database.usecase.favorite.SaveQueryAsFavoriteDatabaseUseCase
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseRowUpdatedNotifier
import io.github.openflocon.flocondesktop.features.database.mapper.canEditColumn
import io.github.openflocon.flocondesktop.features.database.mapper.canEditRow
import io.github.openflocon.flocondesktop.features.database.mapper.displayRowUpdateResult
import io.github.openflocon.flocondesktop.features.database.mapper.toDomain
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabaseCellEditUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseResultToCsvProcessor
import io.github.openflocon.flocondesktop.features.database.processor.ImportSqlQueryProcessor
import io.github.openflocon.flocondesktop.telemetry.TelemetryFeature
import io.github.openflocon.flocondesktop.telemetry.TelemetryManager
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.getString
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

class DatabaseTabViewModel(
    private val params: Params,
    private val executeDatabaseQueryUseCase: ExecuteDatabaseQueryUseCase,
    private val saveAsFavoriteUseCase: SaveQueryAsFavoriteDatabaseUseCase,
    private val getFavoriteQueryUseCase: GetFavoriteQueryByIdDatabaseUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeLastSuccessQueriesUseCase: ObserveLastSuccessQueriesUseCase,
    private val importSqlQueryProcessor: ImportSqlQueryProcessor,
    private val exportDatabaseResultToCsv: ExportDatabaseResultToCsvProcessor,
    private val telemetryManager: TelemetryManager,
    private val getRowEditTargetUseCase: GetRowEditTargetUseCase,
    private val rowUpdatedNotifier: DatabaseRowUpdatedNotifier,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val updateDatabaseRowUseCase: UpdateDatabaseRowUseCase,
) : ViewModel() {

    @Immutable
    data class Params(
        val databaseId: String,
        val tableName: String?,
        val favoriteId: Long?,
        val query: String?,
    )

    var query = mutableStateOf("")

    val lastQueries = observeLastSuccessQueriesUseCase(params.databaseId)
        .map { it.filterNot { it.isBlank() }.toImmutableList() }
        .flowOn(dispatcherProvider.data)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            persistentListOf()
        )

    private val autoUpdateJob = AtomicReference<Job?>(null)

    data class AutoUpdate(
        val query: String? = null,
        val isEnabled: Boolean = false,
    )

    private val isVisible = MutableStateFlow(false)

    fun onVisible() {
        isVisible.update { true }
    }

    fun onNotVisible() {
        isVisible.update { false }
    }

    private val autoUpdate = MutableStateFlow(AutoUpdate())
    val isAutoUpdateEnabled = autoUpdate
        .map { it.isEnabled }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false,
        )

    private val queryResult = MutableStateFlow<QueryResultUiModel?>(null)

    private val cellEdit = MutableStateFlow<DatabaseCellEditUiModel?>(null)

    /** One query at a time, so that the result and its edit target are the ones of the last query run. */
    private val queryMutex = Mutex()

    private data class EditTarget(
        val device: DeviceIdAndPackageNameDomainModel,
        val query: String,
        val columns: List<String>,
        val target: DatabaseRowEditTargetUiModel?,
    )

    /**
     * The one of the last query, kept so that the auto update doesn't read the table definition each time.
     * Guarded by [queryMutex].
     */
    private var lastEditTarget: EditTarget? = null

    val state: StateFlow<DatabaseScreenState> = combine(queryResult, cellEdit) { queryResult, cellEdit ->
        if (queryResult == null) {
            DatabaseScreenState.Idle
        } else {
            DatabaseScreenState.Result(result = queryResult, cellEdit = cellEdit)
        }
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = DatabaseScreenState.Idle,
        )

    init {
        params.query?.let { query ->
            updateQuery(query)
            // DO NOT EXECUTE QUERY HERE viewModelScope.launch(dispatcherProvider.viewModel) {
            // DO NOT EXECUTE QUERY HERE     executeQuery(query = query, editAutoUpdate = true)
            // DO NOT EXECUTE QUERY HERE }
        }
        params.favoriteId?.let {
            viewModelScope.launch(dispatcherProvider.viewModel) {
                getFavoriteQueryUseCase(
                    id = it,
                    databaseId = params.databaseId,
                )?.let {
                    val q = it.query
                    updateQuery(q)
                    executeQuery(query = q, editAutoUpdate = true)
                }
            }
        }
        params.tableName?.let {
            val query = buildString {
                appendLine("SELECT * ")
                appendLine("FROM $it")
                append("LIMIT 50 OFFSET 0")
            }
            updateQuery(query)
            viewModelScope.launch(dispatcherProvider.viewModel) {
                executeQuery(query = query, editAutoUpdate = true)
            }
        }
    }

    fun onAction(action: DatabaseTabAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is DatabaseTabAction.ClearQuery -> clearQuery()

                is DatabaseTabAction.ExecuteQuery -> {
                    trackQueryExecutedByUser()
                    updateQuery(action.query)
                    executeQuery(
                        query = action.query,
                        editAutoUpdate = true
                    )
                }

                is DatabaseTabAction.UpdateAutoUpdate -> updateAutoUpdate(action.value)

                DatabaseTabAction.Copy -> {
                    copyToClipboard(query.value)
                    feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
                }

                DatabaseTabAction.Import -> {
                    importSqlQueryProcessor()?.let { q ->
                        trackQueryExecutedByUser()
                        updateQuery(q)
                        executeQuery(
                            query = q,
                            editAutoUpdate = true
                        )
                    }
                }

                is DatabaseTabAction.SaveFavorite -> {
                    saveAsFavoriteUseCase(
                        title = action.title,
                        query = query.value,
                        databaseId = params.databaseId,
                    )
                }

                DatabaseTabAction.ExportCsv -> {
                    (queryResult.value as? QueryResultUiModel.Values)?.let {
                        exportDatabaseResultToCsv(
                            columns = it.columns,
                            values = it.rows.map { row ->
                                row.items.map { it ?: "NULL" }
                            }
                        ).fold(
                            doOnSuccess = {
                                feedbackDisplayer.displayMessage("exported to $it")
                            },
                            doOnFailure = {
                                feedbackDisplayer.displayMessage("export failed : $it")
                            }
                        )
                    }
                }

                is DatabaseTabAction.StartCellEdit -> startCellEdit(rowIndex = action.rowIndex, columnIndex = action.columnIndex)

                DatabaseTabAction.CancelCellEdit -> cellEdit.value = null

                is DatabaseTabAction.CommitCellEdit -> {
                    val edit = cellEdit.value
                    if (edit != null && !edit.isSaving && edit.isAt(action.rowIndex, action.columnIndex)) {
                        writeCell(rowIndex = action.rowIndex, columnIndex = action.columnIndex, value = action.value)
                    }
                }

                is DatabaseTabAction.SetCellNull -> writeCell(rowIndex = action.rowIndex, columnIndex = action.columnIndex, value = null)

                is DatabaseTabAction.CopyCellValue -> {
                    copyToClipboard(action.value)
                    feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
                }
            }
        }
    }

    /** The row and the column of the cell, when it can be written back. */
    private fun editableCell(rowIndex: Int, columnIndex: Int): Pair<DatabaseRowEditTargetUiModel, List<String?>>? {
        val result = queryResult.value as? QueryResultUiModel.Values ?: return null
        val target = result.editTarget ?: return null
        val row = result.rows.getOrNull(rowIndex)?.items ?: return null
        if (!target.canEditRow(row) || !target.canEditColumn(columnIndex)) return null
        return target to row
    }

    private fun startCellEdit(rowIndex: Int, columnIndex: Int) {
        if (editableCell(rowIndex, columnIndex) == null) return
        cellEdit.value = DatabaseCellEditUiModel(rowIndex = rowIndex, columnIndex = columnIndex)
    }

    private suspend fun writeCell(rowIndex: Int, columnIndex: Int, value: String?) {
        val (target, original) = editableCell(rowIndex, columnIndex) ?: return
        val isEdited = { edit: DatabaseCellEditUiModel? -> edit?.isAt(rowIndex, columnIndex) == true }
        if (original[columnIndex] == value) {
            cellEdit.update { it.takeUnless(isEdited) }
            return
        }
        cellEdit.update { if (isEdited(it)) it?.copy(isSaving = true) else it }

        val update = updateDatabaseRowUseCase(
            target = target.toDomain(),
            original = original,
            edited = original.toMutableList().apply { set(columnIndex, value) },
        )
        if (update == DatabaseRowUpdateResultDomainModel.Updated) {
            // the rows are read again by every tab showing this table, this one included
            rowUpdatedNotifier.notifyUpdated(target)
        }
        feedbackDisplayer.displayRowUpdateResult(update)

        cellEdit.update {
            when {
                !isEdited(it) -> it
                update is DatabaseRowUpdateResultDomainModel.InvalidNumber -> it?.copy(isSaving = false, isInvalid = true)
                else -> null
            }
        }
    }

    /**
     * Only a query the user ran (button, import), never the automatic ones (table opened, favorite loaded,
     * auto-update). Only the fact that a query ran, never the query.
     */
    private fun trackQueryExecutedByUser() {
        telemetryManager.featureUsed(TelemetryFeature.DatabaseQueryExecuted)
    }

    fun updateQuery(queryValue: String) {
        this.query.value = queryValue
    }

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            combines(isVisible, autoUpdate)
                .distinctUntilChanged()
                .collect { (isVisible, autoUpdate) ->
                    refreshAutoUpdate(
                        isVisible = isVisible,
                        autoUpdate = autoUpdate,
                    )
                }
        }
    }

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            rowUpdatedNotifier.updates.collect { updated ->
                refreshAfterUpdate(updated)
            }
        }
    }

    /** Reads the rows again when they come from the updated table, of the same app of the same device. */
    private suspend fun refreshAfterUpdate(updated: DatabaseRowEditTargetUiModel) = queryMutex.withLock {
        val query = autoUpdate.value.query ?: return@withLock
        val shown = lastEditTarget?.takeIf { it.query == query }?.target ?: return@withLock
        val isSameTable = shown.deviceId == updated.deviceId &&
            shown.packageName == updated.packageName &&
            shown.databaseId == updated.databaseId &&
            shown.tableName.equals(updated.tableName, ignoreCase = true)
        if (!isSameTable) return@withLock

        // another device may have been selected since: its rows must not replace the ones shown
        val current = getCurrentDeviceIdAndPackageNameUseCase() ?: return@withLock
        if (current.deviceId != shown.deviceId || current.packageName != shown.packageName) return@withLock

        runQuery(query = query, device = current, editAutoUpdate = false)
    }

    private suspend fun executeQuery(query: String, editAutoUpdate: Boolean) = queryMutex.withLock {
        // resolved once: the rows and their edit target must come from the same device
        val device = getCurrentDeviceIdAndPackageNameUseCase()
        if (device == null) {
            feedbackDisplayer.displayMessage("database failure : no current device")
        } else {
            runQuery(query = query, device = device, editAutoUpdate = editAutoUpdate)
        }
    }

    private suspend fun runQuery(
        query: String,
        device: DeviceIdAndPackageNameDomainModel,
        editAutoUpdate: Boolean,
    ) {
        println("executeQuery: $query")
        executeDatabaseQueryUseCase(
            query = query,
            databaseId = params.databaseId,
            deviceIdAndPackageName = device,
        ).fold(doOnSuccess = {
            queryResult.value = when (val result = it.toUi()) {
                is QueryResultUiModel.Text -> result

                is QueryResultUiModel.Values -> result.copy(
                    editTarget = getEditTarget(device = device, query = query, columns = result.columns, refresh = editAutoUpdate),
                )
            }
            if (editAutoUpdate) {
                // another query: its rows are not the ones of the cell
                cellEdit.value = null
                autoUpdate.update {
                    it.copy(
                        query = query,
                    )
                }
            }
        }, doOnFailure = {
            feedbackDisplayer.displayMessage("database failure : $it")
        })
    }

    private suspend fun getEditTarget(
        device: DeviceIdAndPackageNameDomainModel,
        query: String,
        columns: List<String>,
        refresh: Boolean,
    ): DatabaseRowEditTargetUiModel? {
        lastEditTarget
            ?.takeIf { !refresh && it.device == device && it.query == query && it.columns == columns }
            ?.let { return it.target }

        return getRowEditTargetUseCase(
            deviceIdAndPackageName = device,
            databaseId = params.databaseId,
            query = query,
            resultColumns = columns,
        ).fold(
            // not kept, to ask again with the next execution
            doOnFailure = {
                lastEditTarget = null
                null
            },
            doOnSuccess = { domain ->
                domain?.toUi().also {
                    lastEditTarget = EditTarget(device = device, query = query, columns = columns, target = it)
                }
            },
        )
    }

    private suspend fun clearQuery() {
        updateQuery("")
        queryResult.update { null }
        cellEdit.value = null
        autoUpdate.update {
            it.copy(
                query = null,
                isEnabled = false,
            )
        }
    }

    private fun updateAutoUpdate(value: Boolean) {
        autoUpdate.update {
            it.copy(
                isEnabled = value,
            )
        }
    }

    private fun refreshAutoUpdate(isVisible: Boolean, autoUpdate: AutoUpdate) {
        val job = autoUpdateJob.get()
        if (!autoUpdate.isEnabled || !isVisible) {
            job?.cancel()
            return
        } else {
            job?.cancel()
            val autoUpdateJob = viewModelScope.launch(dispatcherProvider.viewModel) {
                while (isActive) {
                    delay(3.seconds)
                    val query = this@DatabaseTabViewModel.autoUpdate.value.takeIf { it.isEnabled }?.query ?: return@launch
                    executeQuery(query, editAutoUpdate = false)
                }
            }
            this.autoUpdateJob.set(autoUpdateJob)
        }
    }
}

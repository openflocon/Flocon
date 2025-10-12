package io.github.openflocon.flocondesktop.features.database

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.database.usecase.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.domain.database.usecase.favorite.GetFavoriteQueryByIdDatabaseUseCase
import io.github.openflocon.domain.database.usecase.favorite.SaveQueryAsFavoriteDatabaseUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
) : ViewModel() {

    @Immutable
    data class Params(
        val databaseId: String,
        val tableName: String?,
        val favoriteId: Long?,
    )

    var query = mutableStateOf("")

    val lastQueries = observeLastSuccessQueriesUseCase(params.databaseId)
        .map { it.filterNot { it.isBlank() } }
        .flowOn(dispatcherProvider.data)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            emptyList()
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

    private val _autoUpdate = MutableStateFlow(AutoUpdate())
    val isAutoUpdateEnabled = _autoUpdate
        .map { it.isEnabled }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false,
        )

    private val queryResult = MutableStateFlow<QueryResultUiModel?>(null)

    val state: StateFlow<DatabaseScreenState> = queryResult.map { queryResult ->
        if (queryResult == null) DatabaseScreenState.Idle
        else DatabaseScreenState.Result(queryResult)
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = DatabaseScreenState.Idle,
        )

    init {
        params.favoriteId?.let {
            viewModelScope.launch(dispatcherProvider.viewModel) {
                getFavoriteQueryUseCase(
                    id = it,
                    databaseId = params.databaseId,
                )?.let {
                    val q = it.query
                    query.value = q
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
                    query.value = action.query
                    updateQuery(action.query)
                    executeQuery(
                        query = action.query,
                        editAutoUpdate = true
                    )
                }

                is DatabaseTabAction.UpdateAutoUpdate -> updateAutoUpdate(action.value)
                DatabaseTabAction.Copy -> {
                    copyToClipboard(query.value)
                    feedbackDisplayer.displayMessage("copied")
                }

                DatabaseTabAction.Import -> {
                    // TODO
                }

                is DatabaseTabAction.SaveFavorite -> {
                    saveAsFavoriteUseCase(
                        title = action.title,
                        query = query.value,
                        databaseId = params.databaseId,
                    )
                }
            }
        }
    }

    fun updateQuery(queryValue: String) {
        this.query.value = queryValue
    }

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            combines(isVisible, _autoUpdate)
                .distinctUntilChanged()
                .collect { (isVisible, autoUpdate) ->
                    refreshAutoUpdate(
                        isVisible = isVisible,
                        autoUpdate = autoUpdate,
                    )
                }
        }
    }

    private suspend fun executeQuery(query: String, editAutoUpdate: Boolean) {
        println("executeQuery: $query")
        executeDatabaseQueryUseCase(
            query = query,
            databaseId = params.databaseId,
        ).fold(doOnSuccess = {
            queryResult.value = it.toUi()
            if (editAutoUpdate) {
                _autoUpdate.update {
                    it.copy(
                        query = query,
                    )
                }
            }
        }, doOnFailure = {
            feedbackDisplayer.displayMessage("database failure : $it")
        })
    }

    private suspend fun clearQuery() {
        updateQuery("")
        queryResult.update { null }
        _autoUpdate.update {
            it.copy(
                query = null,
                isEnabled = false,
            )
        }
    }

    private fun updateAutoUpdate(value: Boolean) {
        _autoUpdate.update {
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
                    val query = _autoUpdate.value.takeIf { it.isEnabled }?.query ?: return@launch
                    executeQuery(query, editAutoUpdate = false)
                }
            }
            this.autoUpdateJob.set(autoUpdateJob)
        }
    }

}

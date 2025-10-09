package io.github.openflocon.flocondesktop.features.database

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class DatabaseTabViewModel(
    private val params: Params,
    private val executeDatabaseQueryUseCase: ExecuteDatabaseQueryUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel() {

    @Immutable
    data class Params(
        val databaseId: String,
        val tableName: String?,
    )

    var query = mutableStateOf("")

    data class AutoUpdate(
        val query: String? = null,
        val isEnabled: Boolean = false,
        val autoUpdateJob: Job? = null
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
        params.tableName?.let {
            updateQuery(buildString {
                appendLine("SELECT * ")
                appendLine("FROM $it")
                append("LIMIT 50 OFFSET 0")
            })
            executeQuery()
        }
    }

    fun updateQuery(queryValue: String) {
        this.query.value = queryValue
    }

    fun executeQuery() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            executeQuery(query = query.value, editAutoUpdate = true)
        }
    }

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            combines(isVisible, _autoUpdate).collect { (isVisible, autoUpdate) ->
                refreshAutoUpdate(
                    isVisible = isVisible
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

    fun clearQuery() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            updateQuery("")
            queryResult.update { null }
            _autoUpdate.update {
                it.copy(
                    query = null,
                    isEnabled = false,
                )
            }
        }
    }

    fun updateAutoUpdate(value: Boolean) {
        _autoUpdate.update {
            it.copy(
                isEnabled = value,
            )
        }
    }

    private fun refreshAutoUpdate(isVisible: Boolean) {
        if (!_autoUpdate.value.isEnabled || !isVisible) {
            _autoUpdate.value.autoUpdateJob?.cancel()
            return
        } else {
            _autoUpdate.value.autoUpdateJob?.cancel()
            val autoUpdateJob = viewModelScope.launch(dispatcherProvider.viewModel) {
                while (isActive) {
                    delay(3.seconds)
                    val query = _autoUpdate.value.takeIf { it.isEnabled }?.query ?: return@launch
                    executeQuery(query, editAutoUpdate = false)
                }
            }
            _autoUpdate.update {
                it.copy(
                    autoUpdateJob = autoUpdateJob
                )
            }
        }
    }

}

package io.github.openflocon.flocondesktop.features.database

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.usecase.ExecuteDatabaseQueryUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.mapper.toUi
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            updateQuery("SELECT * FROM $it")
            executeQuery()
        }
    }

    fun updateQuery(queryValue: String) {
        this.query.value = queryValue
    }

    fun executeQuery() {
        executeQuery(query = query.value)
    }

    private fun executeQuery(query: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            executeDatabaseQueryUseCase(
                query = query,
                databaseId = params.databaseId,
            ).fold(doOnSuccess = {
                queryResult.value = it.toUi()
            }, doOnFailure = {
                feedbackDisplayer.displayMessage("database failure : $it")
            })
        }
    }

    fun clearQuery() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            queryResult.update { null }
        }
    }

}

package io.github.openflocon.flocondesktop.features.database.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.domain.ExecuteDatabaseQueryUseCase
import io.github.openflocon.flocondesktop.features.database.domain.ObserveLastSuccessQueriesUseCase
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.ui.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.ui.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.ui.model.QueryResultUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseSelectorDelegate: DatabaseSelectorDelegate,
    private val executeDatabaseQueryUseCase: ExecuteDatabaseQueryUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val observeLastSuccessQueriesUseCase: ObserveLastSuccessQueriesUseCase,
) : ViewModel(databaseSelectorDelegate) {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> = databaseSelectorDelegate.deviceDataBases

    private val queryResult = MutableStateFlow<QueryResultUiModel?>(null)

    val recentQueries: StateFlow<List<String>> = observeLastSuccessQueriesUseCase()
        .map { it.take(5) }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val state: StateFlow<DatabaseScreenState> = queryResult.map { queryResult ->
        if (queryResult == null) DatabaseScreenState.Idle
        else DatabaseScreenState.Result(queryResult)
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DatabaseScreenState.Idle,
        )

    fun executeQuery(query: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            executeDatabaseQueryUseCase(
                query = query,
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

    private fun DatabaseExecuteSqlResponseDomainModel.toUi(): QueryResultUiModel = when (this) {
        is DatabaseExecuteSqlResponseDomainModel.Error -> QueryResultUiModel.Text(this.message)
        is DatabaseExecuteSqlResponseDomainModel.Insert -> QueryResultUiModel.Text("Inserted (insertedId=$insertedId)")
        DatabaseExecuteSqlResponseDomainModel.RawSuccess -> QueryResultUiModel.Text("Success")
        is DatabaseExecuteSqlResponseDomainModel.Select ->
            QueryResultUiModel.Values(
                columns = this.columns,
                rows =
                    values.map {
                        DatabaseRowUiModel(it)
                    },
            )

        is DatabaseExecuteSqlResponseDomainModel.UpdateDelete -> QueryResultUiModel.Text("Done, affected=$affectedCount")
    }

    fun onDatabaseSelected(database: DeviceDataBaseUiModel) {
        databaseSelectorDelegate.onDatabaseSelected(database)
    }

    fun onVisible() {
        databaseSelectorDelegate.start()
    }

    fun onNotVisible() {
        databaseSelectorDelegate.stop()
    }
}

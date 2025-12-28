package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.usecase.ObserveDatabaseQueryLogsUseCase
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class DatabaseQueryLogsViewModel(
    private val dbName: String,
    private val observeDatabaseQueryLogsUseCase: ObserveDatabaseQueryLogsUseCase,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _showTransactions = MutableStateFlow(false)
    val showTransactions = _showTransactions.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val logs: Flow<PagingData<DatabaseQueryUiModel>> =
        _showTransactions.flatMapLatest { showTransactions ->
            observeDatabaseQueryLogsUseCase(dbName, showTransactions)
        }
            .map {
                it.map {
                    it.toUi()
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .cachedIn(viewModelScope)

    fun toggleShowTransactions() {
        _showTransactions.value = !_showTransactions.value
    }
}

private fun DatabaseQueryLogDomainModel.toUi() = DatabaseQueryUiModel(
    sqlQuery = sqlQuery,
    bindArgs = bindArgs,
    timestamp = timestamp,
    isTransaction = isTransaction,
)

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

    private val _filterChips = MutableStateFlow<List<String>>(emptyList())
    val filterChips = _filterChips.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val logs: Flow<PagingData<DatabaseQueryUiModel>> =
        combine(_showTransactions, _filterChips) { showTransactions, filterChips ->
            showTransactions to filterChips
        }.flatMapLatest { (showTransactions, filterChips) ->
            observeDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = showTransactions,
                keywords = filterChips
            )
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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addFilterChip() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty() && !_filterChips.value.contains(query)) {
            _filterChips.value = _filterChips.value + query
            _searchQuery.value = ""
        }
    }

    fun removeFilterChip(chip: String) {
        _filterChips.value = _filterChips.value - chip
    }
}

private fun DatabaseQueryLogDomainModel.toUi() = DatabaseQueryUiModel(
    sqlQuery = sqlQuery,
    bindArgs = bindArgs,
    timestamp = timestamp,
    isTransaction = isTransaction,
)

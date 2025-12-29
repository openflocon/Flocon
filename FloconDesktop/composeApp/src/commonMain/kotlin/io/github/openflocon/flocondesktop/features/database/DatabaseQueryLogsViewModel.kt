package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.usecase.ObserveDatabaseQueryLogsUseCase
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseQueryLogsViewModel(
    private val dbName: String,
    private val observeDatabaseQueryLogsUseCase: ObserveDatabaseQueryLogsUseCase,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
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
        combines(
            _showTransactions,
            _filterChips,
            observeCurrentDeviceIdAndPackageNameUseCase(),
        ).flatMapLatest { (showTransactions, filterChips, currentDeviceAndPackage) ->
            observeDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = showTransactions,
                keywords = filterChips
            ).map {
                it.map {
                    it.toUi(currentDeviceAndPackage = currentDeviceAndPackage)
                }
            }
        }
            .flowOn(dispatcherProvider.viewModel)
            .cachedIn(viewModelScope)

    fun toggleShowTransactions() {
        _showTransactions.update { !it }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addFilterChip() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty() && !_filterChips.value.contains(query)) {
            _filterChips.update { it + query }
            _searchQuery.value = ""
        }
    }

    fun removeFilterChip(chip: String) {
        _filterChips.update { it - chip }
    }
}

private fun DatabaseQueryLogDomainModel.toUi(currentDeviceAndPackage: DeviceIdAndPackageNameDomainModel?) = DatabaseQueryUiModel(
    sqlQuery = sqlQuery,
    bindArgs = bindArgs,
    dateFormatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp)),
    isTransaction = isTransaction,
    isFromOldSession = currentDeviceAndPackage?.appInstance != appInstance,
    type = findType(this),
)


private fun findType(model: DatabaseQueryLogDomainModel): DatabaseQueryUiModel.Type? {
    return if(model.isTransaction) {
        DatabaseQueryUiModel.Type.Transaction
    } else if(model.sqlQuery.startsWith("SELECT")) {
        DatabaseQueryUiModel.Type.Select
    } else if(model.sqlQuery.startsWith("INSERT")) {
        DatabaseQueryUiModel.Type.Insert
    } else if(model.sqlQuery.startsWith("UPDATE")) {
        DatabaseQueryUiModel.Type.Update
    } else if(model.sqlQuery.startsWith("DELETE")) {
        DatabaseQueryUiModel.Type.Delete
    } else {
        null
    }
}

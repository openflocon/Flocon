package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.usecase.ClearDatabaseQueryLogsUseCase
import io.github.openflocon.domain.database.usecase.CountDatabaseQueryLogsUseCase
import io.github.openflocon.domain.database.usecase.GetDatabaseQueryLogsUseCase
import io.github.openflocon.domain.database.usecase.ObserveDatabaseQueryLogsUseCase
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.FilterChipUiModel
import io.github.openflocon.flocondesktop.features.database.model.toDomain
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseQueryLogsToCsvProcessor
import io.github.openflocon.flocondesktop.features.database.processor.ExportDatabaseQueryLogsToMarkdownProcessor
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import io.github.openflocon.domain.database.utils.injectSqlArgs
import kotlinx.coroutines.flow.StateFlow

class DatabaseQueryLogsViewModel(
    private val dbName: String,
    private val observeDatabaseQueryLogsUseCase: ObserveDatabaseQueryLogsUseCase,
    private val countDatabaseQueryLogsUseCase: CountDatabaseQueryLogsUseCase,
    private val getDatabaseQueryLogsUseCase: GetDatabaseQueryLogsUseCase,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val exportDatabaseQueryLogsToCsvProcessor: ExportDatabaseQueryLogsToCsvProcessor,
    private val exportDatabaseQueryLogsToMarkdownProcessor: ExportDatabaseQueryLogsToMarkdownProcessor,
    private val clearDatabaseQueryLogsUseCase: ClearDatabaseQueryLogsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _showTransactions = MutableStateFlow(false)
    val showTransactions = _showTransactions.asStateFlow()

    private val _filterChips = MutableStateFlow<List<FilterChipUiModel>>(emptyList())
    val filterChips = _filterChips.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _page = MutableStateFlow(0)
    val page = _page.asStateFlow()

    private val PAGE_SIZE = 100

    private val filtersFlow = combines(
        _showTransactions,
        _filterChips.map { it.map { it.toDomain() } }.distinctUntilChanged(),
        observeCurrentDeviceIdAndPackageNameUseCase(),
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val logs: StateFlow<List<DatabaseQueryUiModel>> =
        combines(
            filtersFlow,
            _page,
        ).flatMapLatest { (filter, page) ->
            val (showTransactions, filterChips, currentDeviceAndPackage) = filter
            observeDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = showTransactions,
                filters = filterChips,
                limit = PAGE_SIZE,
                offset = page * PAGE_SIZE,
            ).map { list ->
                list.map {
                    it.toUi(currentDeviceAndPackage = currentDeviceAndPackage)
                }
            }
        }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val totalCount: Flow<Int> =
        filtersFlow.flatMapLatest { (showTransactions, filterChips, _) ->
            countDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = showTransactions,
                filters = filterChips,
            )
        }
            .flowOn(dispatcherProvider.viewModel)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPages = totalCount.map { (it + PAGE_SIZE - 1) / PAGE_SIZE }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val paginationLabel = combine(page, totalCount) { page, totalCount ->
        val start = page * PAGE_SIZE
        val end = minOf((page + 1) * PAGE_SIZE, totalCount)
        if (totalCount == 0) "0 - 0" else "${start + 1} - $end"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0 - 0")

    fun nextPage() {
        if (_page.value < totalPages.value - 1) {
            _page.update { it + 1 }
        }
    }

    fun previousPage() {
        if (_page.value > 0) {
            _page.update { it - 1 }
        }
    }

    fun toggleShowTransactions() {
        _showTransactions.update { !it }
        _page.value = 0
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addIncludeFilter() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty() && !_filterChips.value.any { it.text == query }) {
            _filterChips.update { it + FilterChipUiModel(query, FilterChipUiModel.FilterType.INCLUDE) }
            _searchQuery.value = ""
            _page.value = 0
        }
    }
    
    fun addExcludeFilter() {
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty() && !_filterChips.value.any { it.text == query }) {
            _filterChips.update { it + FilterChipUiModel(query, FilterChipUiModel.FilterType.EXCLUDE) }
            _searchQuery.value = ""
            _page.value = 0
        }
    }
    
    fun toggleFilterType(chip: FilterChipUiModel) {
        _filterChips.update { list ->
            list.map {
                if (it == chip) {
                    it.copy(type = if (it.type == FilterChipUiModel.FilterType.INCLUDE)
                        FilterChipUiModel.FilterType.EXCLUDE
                    else
                        FilterChipUiModel.FilterType.INCLUDE
                    )
                } else {
                    it
                }
            }
        }
        _page.value = 0
    }
    
    fun addFilter(text: String, type: FilterChipUiModel.FilterType) {
         if (text.isNotEmpty() && !_filterChips.value.any { it.text == text }) {
            _filterChips.update { it + FilterChipUiModel(text, type) }
            _page.value = 0
        }
    }

    fun removeFilterChip(chip: FilterChipUiModel) {
        _filterChips.update { it - chip }
        _page.value = 0
    }

    fun copyQuery(query: String) {
        copyToClipboard(query)
        feedbackDisplayer.displayMessage("Query copied to clipboard")
    }

    fun copyArgs(args: List<String>?) {
        val argsString = args?.toString() ?: "[]"
        copyToClipboard(argsString)
        feedbackDisplayer.displayMessage("Arguments copied to clipboard")
    }

    fun copyAsSql(query: String, args: List<String>?) {
        val fullSql = injectSqlArgs(query, args)
        copyToClipboard(fullSql)
        feedbackDisplayer.displayMessage("SQL with arguments copied to clipboard")
    }

    fun exportToCsv() {
        viewModelScope.launch {
            val logs = getDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = _showTransactions.value,
                filters = _filterChips.value.map { it.toDomain() }
            )
            exportDatabaseQueryLogsToCsvProcessor(logs).fold(
                doOnSuccess = {
                    feedbackDisplayer.displayMessage("Logs exported to $it")
                },
                doOnFailure = {
                    feedbackDisplayer.displayMessage("Export failed: ${it.message}")
                }
            )
        }
    }

    fun exportToMarkdown() {
        viewModelScope.launch {
            val logs = getDatabaseQueryLogsUseCase(
                dbName = dbName,
                showTransactions = _showTransactions.value,
                filters = _filterChips.value.map { it.toDomain() }
            )
            exportDatabaseQueryLogsToMarkdownProcessor(logs).fold(
                doOnSuccess = {
                    feedbackDisplayer.displayMessage("Logs exported to $it")
                },
                doOnFailure = {
                    feedbackDisplayer.displayMessage("Export failed: ${it.message}")
                }
            )
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            clearDatabaseQueryLogsUseCase()
        }
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

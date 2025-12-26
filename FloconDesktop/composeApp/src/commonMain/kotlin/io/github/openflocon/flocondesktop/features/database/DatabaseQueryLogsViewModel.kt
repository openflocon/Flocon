package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.usecase.GetDatabaseQueryLogsUseCase
import kotlinx.coroutines.flow.Flow

class DatabaseQueryLogsViewModel(
    private val dbName: String,
    private val getDatabaseQueryLogsUseCase: GetDatabaseQueryLogsUseCase,
) : ViewModel() {

    val logs: Flow<PagingData<DatabaseQueryLogDomainModel>> = Pager(
        config = PagingConfig(pageSize = 50),
        pagingSourceFactory = { getDatabaseQueryLogsUseCase(dbName) }
    ).flow.cachedIn(viewModelScope)
}

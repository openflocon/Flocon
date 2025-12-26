package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.domain.database.usecase.ObserveDatabaseQueryLogsUseCase
import kotlinx.coroutines.flow.Flow

class DatabaseQueryLogsViewModel(
    private val dbName: String,
    private val observeDatabaseQueryLogsUseCase: ObserveDatabaseQueryLogsUseCase,
) : ViewModel() {

    val logs: Flow<PagingData<DatabaseQueryLogDomainModel>> =
        observeDatabaseQueryLogsUseCase(dbName)
            .cachedIn(viewModelScope)
}

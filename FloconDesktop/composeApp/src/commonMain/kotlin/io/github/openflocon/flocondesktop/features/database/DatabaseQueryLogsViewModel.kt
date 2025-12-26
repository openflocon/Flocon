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

class DatabaseQueryLogsViewModel(
    dbName: String,
    observeDatabaseQueryLogsUseCase: ObserveDatabaseQueryLogsUseCase,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val logs: Flow<PagingData<DatabaseQueryUiModel>> =
        observeDatabaseQueryLogsUseCase(dbName)
            .map {
                it.map {
                    it.toUi()
                }
            }
            .flowOn(dispatcherProvider.viewModel)
            .cachedIn(viewModelScope)
}

private fun DatabaseQueryLogDomainModel.toUi() = DatabaseQueryUiModel(
    sqlQuery = sqlQuery,
    bindArgs = bindArgs,
    timestamp = timestamp,
)

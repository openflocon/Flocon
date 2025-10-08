package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.database.DatabaseTabViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseTabView(
    databaseId: String,
    tableName: String?,
) {
    val params = remember(databaseId, tableName) {
        DatabaseTabViewModel.Params(
            databaseId = databaseId,
            tableName = tableName,
        )
    }
    val viewModel: DatabaseTabViewModel = koinViewModel(
        key = params.toString(),
        parameters = { parametersOf(params) }
    )

    val state: DatabaseScreenState by viewModel.state.collectAsStateWithLifecycle()
    DatabaseTabViewContent(
        query = viewModel.query.value,
        updateQuery = viewModel::updateQuery,
        executeQuery = viewModel::executeQuery,
        clearQuery = viewModel::clearQuery,
        state = state,
    )
}

@Composable
private fun DatabaseTabViewContent(
    query: String,
    updateQuery: (String) -> Unit,
    executeQuery: () -> Unit,
    clearQuery: () -> Unit,
    state: DatabaseScreenState,
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
        ) { contentPadding ->
            DatabaseQueryView(
                query = query,
                updateQuery = updateQuery,
                executeQuery = {
                    executeQuery()
                },
                clearQuery = clearQuery,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        DatabaseContentView(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


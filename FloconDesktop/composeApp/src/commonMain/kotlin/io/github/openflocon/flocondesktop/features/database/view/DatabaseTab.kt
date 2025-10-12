package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.database.DatabaseTabViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseTabView(
    tab: DatabaseTabState,
    favoritesTitles: Set<String>,
) {
    val viewModel: DatabaseTabViewModel = koinViewModel(
        key = tab.id,
        parameters = { parametersOf(
            DatabaseTabViewModel.Params(
                databaseId = tab.databaseId,
                tableName = tab.tableName,
                favoriteId = tab.favoriteId,
            )
        ) }
    )

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    val state: DatabaseScreenState by viewModel.state.collectAsStateWithLifecycle()
    val autoUpdate by viewModel.isAutoUpdateEnabled.collectAsStateWithLifecycle()
    val lastQueries by viewModel.lastQueries.collectAsStateWithLifecycle()

    DatabaseTabViewContent(
        query = viewModel.query.value,
        autoUpdate = autoUpdate,
        updateQuery = viewModel::updateQuery,
        onAction = viewModel::onAction,
        state = state,
        lastQueries = lastQueries,
        favoritesTitles = favoritesTitles,
    )
}

@Composable
private fun DatabaseTabViewContent(
    query: String,
    autoUpdate: Boolean,
    favoritesTitles: Set<String>,
    updateQuery: (String) -> Unit,
    onAction: (action: DatabaseTabAction) -> Unit,
    state: DatabaseScreenState,
    lastQueries: List<String>,
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
                autoUpdate = autoUpdate,
                onAction = onAction,
                favoritesTitles = favoritesTitles,
                lastQueries = lastQueries,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        DatabaseContentView(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            onExportCsvClicked = {
                onAction(DatabaseTabAction.ExportCsv)
            },
        )
    }
}


package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
) {
    val viewModel: DatabaseTabViewModel = koinViewModel(
        key = tab.id,
        parameters = { parametersOf(
            DatabaseTabViewModel.Params(
                databaseId = tab.databaseId,
                tableName = tab.tableName,
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
    DatabaseTabViewContent(
        query = viewModel.query.value,
        autoUpdate = autoUpdate,
        updateQuery = viewModel::updateQuery,
        onAction = viewModel::onAction,
        state = state,
    )
}

@Composable
private fun DatabaseTabViewContent(
    query: String,
    autoUpdate: Boolean,
    updateQuery: (String) -> Unit,
    onAction: (action: DatabaseTabAction) -> Unit,
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
                autoUpdate = autoUpdate,
                onAction = onAction,
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


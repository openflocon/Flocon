package com.florent37.flocondesktop.features.database.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.database.ui.DatabaseViewModel
import com.florent37.flocondesktop.features.database.ui.model.DatabaseScreenState
import com.florent37.flocondesktop.features.database.ui.model.DatabasesStateUiModel
import com.florent37.flocondesktop.features.database.ui.model.DeviceDataBaseUiModel
import com.florent37.flocondesktop.features.database.ui.model.previewDatabaseScreenStateQueries
import com.florent37.flocondesktop.features.database.ui.model.previewDatabasesStateUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatabaseScreen(modifier: Modifier = Modifier) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val deviceDataBases by viewModel.deviceDataBases.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    DatabaseScreen(
        deviceDataBases = deviceDataBases,
        onDatabaseSelected = viewModel::onDatabaseSelected,
        executeQuery = viewModel::executeQuery,
        clearQuery = viewModel::clearQuery,
        state = state,
        modifier = modifier,
    )
}

@Composable
fun DatabaseScreen(
    deviceDataBases: DatabasesStateUiModel,
    onDatabaseSelected: (DeviceDataBaseUiModel) -> Unit,
    executeQuery: (query: String) -> Unit,
    clearQuery: () -> Unit,
    state: DatabaseScreenState,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }

    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(FloconColors.pannel)
                    .padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Database",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                DatabaseSelectorView(
                    databasesState = deviceDataBases,
                    onDatabaseSelected = onDatabaseSelected,
                    modifier = Modifier.fillMaxWidth(),
                )

                DatabaseQueryView(
                    query = query,
                    updateQuery = {
                        query = it
                    },
                    executeQuery = executeQuery,
                    clearQuery = clearQuery,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            DatabaseContentView(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickQuery = {
                    query = it
                },
            )
        }
    }
}

@Composable
@Preview
private fun DatabaseScreenPreview() {
    FloconTheme {
        DatabaseScreen(
            deviceDataBases = previewDatabasesStateUiModel(),
            onDatabaseSelected = {},
            executeQuery = {},
            clearQuery = {},
            state = DatabaseScreenState.Idle,
        )
    }
}

@Composable
@Preview
private fun DatabaseScreenPreview_Result() {
    FloconTheme {
        DatabaseScreen(
            deviceDataBases = previewDatabasesStateUiModel(),
            onDatabaseSelected = {},
            executeQuery = {},
            clearQuery = {},
            state = DatabaseScreenState.Result(
                result = com.florent37.flocondesktop.features.database.ui.model.QueryResultUiModel.Text(
                    "query result",
                ),
            ),
        )
    }
}

@Composable
@Preview
private fun DatabaseScreenPreview_Queries() {
    FloconTheme {
        DatabaseScreen(
            deviceDataBases = previewDatabasesStateUiModel(),
            onDatabaseSelected = {},
            executeQuery = {},
            clearQuery = {},
            state = previewDatabaseScreenStateQueries(),
        )
    }
}

package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.DatabaseViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.flocondesktop.features.database.model.previewDatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.previewDatabasesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatabaseScreen(modifier: Modifier = Modifier) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val deviceDataBases by viewModel.deviceDataBases.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

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
        recentQueries = recentQueries,
        modifier = modifier,
    )
}

@Composable
fun DatabaseScreen(
    deviceDataBases: DatabasesStateUiModel,
    onDatabaseSelected: (DeviceDataBaseId) -> Unit,
    executeQuery: (query: String) -> Unit,
    clearQuery: () -> Unit,
    recentQueries: List<String>,
    state: DatabaseScreenState,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        Row(Modifier.fillMaxSize()) {
            DatabasesAndTablesView(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                state = deviceDataBases,
                onDatabaseSelected = onDatabaseSelected,
            )
            Spacer(modifier = Modifier.width(12.dp))
            when (deviceDataBases) {
                DatabasesStateUiModel.Empty -> Unit
                DatabasesStateUiModel.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is DatabasesStateUiModel.WithContent -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FloconPageTopBar(
                            modifier = Modifier.fillMaxWidth(),
                        ) { contentPadding ->
                            DatabaseQueryView(
                                query = query,
                                updateQuery = { query = it },
                                executeQuery = executeQuery,
                                clearQuery = clearQuery,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                        recentQueries.takeIf { it.isNotEmpty() }
                            ?.let {
                                DatabaseQueriesView(
                                    queries = recentQueries,
                                    onClickQuery = {
                                        query = it
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                        DatabaseContentView(
                            state = state,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
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
            recentQueries = emptyList(),
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
                result = QueryResultUiModel.Text(
                    "query result",
                ),
            ),
            recentQueries = listOf("SELECT * FROM TOTO", "SELECT * FROM TATA"),
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
            recentQueries = listOf("SELECT * FROM TOTO", "SELECT * FROM TATA"),
            state = previewDatabaseScreenState(),
        )
    }
}

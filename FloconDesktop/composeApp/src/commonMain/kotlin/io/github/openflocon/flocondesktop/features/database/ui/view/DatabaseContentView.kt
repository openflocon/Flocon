package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabaseScreenState

@Composable
fun DatabaseContentView(
    state: DatabaseScreenState,
    onClickQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DatabaseScreenState.Idle -> {
            // nothing
        }

        is DatabaseScreenState.Queries -> {
            DatabaseQueriesView(
                queries = state.queries,
                modifier = modifier,
                onClickQuery = onClickQuery,
            )
        }

        is DatabaseScreenState.Result -> {
            DatabaseResultView(
                result = state.result,
                modifier = modifier,
            )
        }
    }
}

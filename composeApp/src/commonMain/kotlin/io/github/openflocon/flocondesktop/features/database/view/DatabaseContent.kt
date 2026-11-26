package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction

@Composable
fun DatabaseContent(
    state: DatabaseScreenState,
    onAction: (DatabaseTabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DatabaseScreenState.Idle -> {
            // nothing
        }

        is DatabaseScreenState.Result -> {
            DatabaseResult(
                result = state.result,
                cellEdit = state.cellEdit,
                modifier = modifier,
                onAction = onAction,
            )
        }
    }
}

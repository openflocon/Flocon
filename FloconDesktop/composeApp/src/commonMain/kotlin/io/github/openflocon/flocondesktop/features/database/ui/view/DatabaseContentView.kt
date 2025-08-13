package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabaseScreenState

@Composable
fun DatabaseContentView(
    state: DatabaseScreenState,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DatabaseScreenState.Idle -> {
            // nothing
        }

        is DatabaseScreenState.Result -> {
            DatabaseResultView(
                result = state.result,
                modifier = modifier,
            )
        }
    }
}

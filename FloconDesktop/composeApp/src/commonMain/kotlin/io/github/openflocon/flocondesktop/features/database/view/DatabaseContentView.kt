package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenState

@Composable
fun DatabaseContentView(
    state: DatabaseScreenState,
    onExportCsvClicked: () -> Unit,
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
                onExportCsvClicked = onExportCsvClicked,
            )
        }
    }
}

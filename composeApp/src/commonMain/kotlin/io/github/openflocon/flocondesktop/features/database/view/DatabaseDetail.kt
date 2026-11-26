package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel

@Composable
fun DatabaseDetails(
    state: DetailResultItem,
    columns: List<String>,
    modifier: Modifier = Modifier,
    editTarget: DatabaseRowEditTargetUiModel? = null,
) {
    DatabaseRowDetail(
        modifier = modifier.fillMaxSize(),
        state = state,
        columns = columns,
        editTarget = editTarget,
    )
}

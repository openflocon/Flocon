package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel

@Composable
fun DatabaseDetails(
    values: List<String?>,
    columns: List<String>,
    modifier: Modifier = Modifier,
    editTarget: DatabaseRowEditTargetUiModel? = null,
) {
    DatabaseRowDetail(
        modifier = modifier.fillMaxSize(),
        values = values,
        columns = columns,
        editTarget = editTarget,
    )
}

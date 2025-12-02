package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DatabaseDetails(
    state: DetailResultItem,
    columns: List<String>
) {
    DatabaseRowDetailView(
        modifier = Modifier.fillMaxSize(),
        state = state,
        columns = columns,
    )
}

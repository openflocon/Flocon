package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.view.logs.DatabaseQueryLogsView

@Composable
fun DatabaseTabView(
    tab: DatabaseTabState,
    favoritesTitles: Set<String>,
) {
    if (tab.isQueryLogs) {
        DatabaseQueryLogsView(
            dbName = tab.databaseName,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        DatabaseTabQueryView(
            tab = tab,
            favoritesTitles = favoritesTitles,
        )
    }
}
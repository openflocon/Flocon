package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.view.logs.DatabaseQueryLogsView
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun DatabaseTabView(
    tab: DatabaseTabState,
    favoritesTitles: ImmutableSet<String>,
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
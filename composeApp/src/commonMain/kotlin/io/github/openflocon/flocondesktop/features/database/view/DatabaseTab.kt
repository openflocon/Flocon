package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.view.logs.DatabaseQueryLogs
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun DatabaseTab(
    tab: DatabaseTabState,
    favoritesTitles: ImmutableSet<String>,
    modifier: Modifier = Modifier,
) {
    if (tab.isQueryLogs) {
        DatabaseQueryLogs(
            dbId = tab.databaseId,
            modifier = modifier.fillMaxSize()
        )
    } else {
        DatabaseTabQuery(
            tab = tab,
            favoritesTitles = favoritesTitles,
            modifier = modifier,
        )
    }
}

package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogsViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseQueryLogsView(
    dbName: String,
    modifier: Modifier = Modifier
) {
    val viewModel: DatabaseQueryLogsViewModel = koinViewModel(
        parameters = { parametersOf(dbName) }
    )

    val logs = viewModel.logs.collectAsLazyPagingItems()

    Box(
        modifier = Modifier.fillMaxSize()
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
            .background(
                color = FloconTheme.colorPalette.primary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logs.itemCount) { index ->
                val log = logs[index]
                if (log != null) {
                    DatabaseQueryLogItemView(
                        log = log,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    HorizontalDivider(color = FloconTheme.colorPalette.secondary)
                }
            }
        }
    }
}

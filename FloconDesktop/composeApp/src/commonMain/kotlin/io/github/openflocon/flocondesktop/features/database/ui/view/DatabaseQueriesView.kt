package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconColorScheme
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.database.ui.model.previewDatabaseScreenStateQueries
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DatabaseQueriesView(
    queries: List<String>,
    onClickQuery: (query: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(all = 8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = "Recent queries",
            color = FloconColorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(all = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(queries) {
                DatabaseQueriesItemView(
                    query = it,
                    onClick = onClickQuery,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun DatabaseQueriesItemView(
    query: String,
    onClick: (query: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = {
                onClick(query)
            })
            .background(FloconColors.pannel)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = query,
            color = FloconColorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
@Preview
private fun DatabaseQueriesViewPreview() {
    FloconTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FloconColors.surface),
        ) {
            DatabaseQueriesView(
                onClickQuery = {},
                modifier = Modifier.fillMaxSize(),
                queries = previewDatabaseScreenStateQueries().queries,
            )
        }
    }
}

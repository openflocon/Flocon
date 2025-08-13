package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

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
            color = FloconTheme.colorPalette.onSurface,
            style = FloconTheme.typography.titleSmall,
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(all = 4.dp),
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
            .background(FloconTheme.colorPalette.panel)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = query,
            color = FloconTheme.colorPalette.onSurface,
            style = FloconTheme.typography.bodySmall,
        )
    }
}

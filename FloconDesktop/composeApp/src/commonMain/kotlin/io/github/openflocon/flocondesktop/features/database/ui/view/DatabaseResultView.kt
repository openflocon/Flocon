package io.github.openflocon.flocondesktop.features.database.ui.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.database.ui.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.ui.model.QueryResultUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DatabaseResultView(
    result: QueryResultUiModel?,
    modifier: Modifier = Modifier,
) {
    val columnsWidth = 150.dp

    if (result == null) {
        return
    }

    SelectionContainer {
        Column(modifier = modifier.horizontalScroll(rememberScrollState())) {
            when (result) {
                is QueryResultUiModel.Text -> {
                    Text(
                        text = result.text,
                        color = FloconTheme.colorPalette.onBackground,
                        modifier = Modifier.padding(8.dp),
                    )
                }

                is QueryResultUiModel.Values -> {
                    // Column Headers
                    Row(
                        modifier = Modifier
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp,
                            ) // Padding for the entire item
                            .clip(shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        // Inner padding for content
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Date - Fixed width from data class
                        result.columns.fastForEach { item ->
                            Text(
                                item,
                                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = FloconTheme.colorPalette.onSurface,
                                modifier = Modifier.width(columnsWidth)
                                    .padding(all = 4.dp),
                            )
                        }
                    }

                    // Data Rows
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(result.rows) { row ->
                            Row(
                                modifier = modifier
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 2.dp,
                                    ) // Padding for the entire item
                                    .clip(shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                // Inner padding for content
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // Date - Fixed width from data class
                                row.items.forEach { item ->
                                    Text(
                                        item ?: "null",
                                        style = FloconTheme.typography.bodySmall,
                                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.width(columnsWidth)
                                            .padding(horizontal = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewText() {
    FloconTheme {
        val result = QueryResultUiModel.Text(text = "This is a sample text result.")
        DatabaseResultView(result = result)
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewValues() {
    val result = QueryResultUiModel.Values(
        columns = listOf("ID", "Name", "Email"),
        rows = listOf(
            DatabaseRowUiModel(items = listOf("1", "John Doe", "john.doe@example.com")),
            DatabaseRowUiModel(items = listOf("2", "Jane Smith", "jane.smith@example.com")),
            DatabaseRowUiModel(items = listOf("3", "Peter Jones", null)),
        ),
    )
    FloconTheme {
        DatabaseResultView(result = result)
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewNull() {
    FloconTheme {
        DatabaseResultView(result = null)
    }
}

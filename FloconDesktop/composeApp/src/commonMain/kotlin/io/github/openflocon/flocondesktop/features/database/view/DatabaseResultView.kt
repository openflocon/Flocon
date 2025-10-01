package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
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
        when (result) {
            is QueryResultUiModel.Text -> {
                Text(
                    text = result.text,
                    color = FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier.padding(8.dp),
                )
            }

            is QueryResultUiModel.Values -> {
                val color = FloconTheme.colorPalette.secondary

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary)
                        .border(
                            width = 1.dp,
                            color = color,
                            shape = FloconTheme.shapes.medium
                        )
                        .horizontalScroll(rememberScrollState())
                ) {
                    stickyHeader {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            result.columns
                                .fastForEachIndexed { index, item ->
                                    Text(
                                        item,
                                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = FloconTheme.colorPalette.onSecondary,
                                        modifier = Modifier
                                            .width(columnsWidth)
                                            .padding(all = 4.dp),
                                    )
                                }
                        }
                    }
                    itemsIndexed(result.rows) { index, row ->
                        Row(
                            modifier = Modifier
                                .height(32.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.items.forEachIndexed { index, item ->
                                Text(
                                    item ?: "null",
                                    style = FloconTheme.typography.bodySmall,
                                    color = FloconTheme.colorPalette.onPrimary,
                                    modifier = Modifier
                                        .width(columnsWidth)
                                        .padding(horizontal = 4.dp),
                                )
                            }
                        }
                        if (index != result.rows.lastIndex) {
                            FloconHorizontalDivider(
                                color = color,
                                modifier = Modifier.fillMaxWidth()
                            )
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

package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.panel.FloconPanel
import org.jetbrains.compose.ui.tooling.preview.Preview

data class DetailResultItem(
    val index: Int,
    val item: DatabaseRowUiModel,
)

@Composable
fun DatabaseResultView(
    result: QueryResultUiModel?,
    onExportCsvClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnsWidth = 200.dp

    if (result == null) {
        return
    }

    SelectionContainer(modifier) {
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

                var selectedItem by remember(result) {
                    mutableStateOf<DetailResultItem?>(null)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary)
                        .border(
                            width = 1.dp,
                            color = color,
                            shape = FloconTheme.shapes.medium
                        )
                ) {
                    ResultHeader(
                        result = result,
                        modifier = Modifier.fillMaxWidth(),
                        onExportCsvClicked = onExportCsvClicked
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState())
                            .onPreviewKeyEvent { event ->
                                if (event.type != KeyEventType.KeyDown)
                                    return@onPreviewKeyEvent false

                                when (event.key) {
                                    Key.DirectionUp -> {
                                        selectedItem?.index?.let { i ->
                                            val newIndex = i - 1
                                            result.rows.getOrNull(newIndex)?.let {
                                                selectedItem = DetailResultItem(
                                                    index = newIndex,
                                                    item = it
                                                )
                                            }
                                        }

                                        true
                                    }

                                    Key.DirectionDown -> {
                                        selectedItem?.index?.let { i ->
                                            val newIndex = i + 1
                                            result.rows.getOrNull(newIndex)?.let {
                                                selectedItem = DetailResultItem(
                                                    index = newIndex,
                                                    item = it
                                                )
                                            }
                                        }

                                        true
                                    }

                                    else -> false
                                }
                            }
                    ) {
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(FloconTheme.colorPalette.primary)
                                    .height(32.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                result.columns
                                    .fastForEachIndexed { index, item ->
                                        Text(
                                            item,
                                            style = FloconTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = FloconTheme.colorPalette.onPrimary,
                                            modifier = Modifier
                                                .width(columnsWidth)
                                                .padding(all = 4.dp),
                                        )
                                    }
                            }
                        }
                        itemsIndexed(result.rows) { index, row ->
                            val selected = index == selectedItem?.index

                            DatabaseResultRowView(
                                index = index,
                                row = row,
                                columnsWidth = columnsWidth,
                                selected = selected,
                                onItemSelected = {
                                    selectedItem = it
                                }
                            )

                            if (index != result.rows.lastIndex) {
                                FloconHorizontalDivider(
                                    color = color,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    FloconPanel(
                        contentState = selectedItem,
                        onClose = {
                            selectedItem = null
                        }
                    ) {
                        DatabaseRowDetailView(
                            modifier = Modifier.fillMaxSize(),
                            state = it,
                            columns = result.columns,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseResultRowView(
    index: Int,
    row: DatabaseRowUiModel,
    columnsWidth: Dp,
    selected: Boolean,
    onItemSelected: (DetailResultItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .padding(horizontal = 8.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.accent,
                        shape = FloconTheme.shapes.medium
                    )
                } else {
                    Modifier
                }
            )
            .clickable {
                onItemSelected(
                    DetailResultItem(
                        index = index,
                        item = row
                    )
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        row.items.forEachIndexed { index, item ->
            Text(
                text = item ?: "NULL",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary,
                modifier = Modifier
                    .width(columnsWidth)
                    .graphicsLayer {
                        if (item == null)
                            alpha = 0.5f
                    }
                    .padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun ResultHeader(
    result: QueryResultUiModel.Values,
    onExportCsvClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .background(FloconTheme.colorPalette.surface)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${result.rows.size} rows",
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = FloconTheme.colorPalette.onPrimary,
            modifier = Modifier
                .padding(all = 4.dp),
        )

        Spacer(Modifier.weight(1f))

        Box(
            Modifier.size(30.dp).clickable {
                onExportCsvClicked()
            }.padding(5.dp)
        ) {
            Image(
                imageVector = Icons.AutoMirrored.Outlined.DriveFileMove,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewText() {
    FloconTheme {
        val result = QueryResultUiModel.Text(text = "This is a sample text result.")
        DatabaseResultView(
            result = result,
            onExportCsvClicked = {},
        )
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
        DatabaseResultView(
            result = result,
            onExportCsvClicked = {},
        )
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewNull() {
    FloconTheme {
        DatabaseResultView(
            result = null,
            onExportCsvClicked = {},
        )
    }
}

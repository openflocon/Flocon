package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_row_set_null
import flocondesktop.composeapp.generated.resources.general_copy_value
import flocondesktop.composeapp.generated.resources.general_edit
import flocondesktop.composeapp.generated.resources.nb_lines
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.database.mapper.canEditColumn
import io.github.openflocon.flocondesktop.features.database.mapper.canEditRow
import io.github.openflocon.flocondesktop.features.database.model.DatabaseCellEditUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.flocondesktop.features.database.model.QueryResultUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.FloconContextMenuItem
import io.github.openflocon.library.designsystem.components.FloconColumnResizeHandle
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import io.github.openflocon.library.designsystem.components.floconTableHeaderBackground
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

private val DefaultColumnWidth = 200.dp
private val MinColumnWidth = 48.dp
private val HandleWidth = 8.dp

@Composable
fun DatabaseResult(
    result: QueryResultUiModel?,
    cellEdit: DatabaseCellEditUiModel?,
    onAction: (DatabaseTabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (result == null) {
        return
    }

    SelectionContainer(modifier) {
        when (result) {
            is QueryResultUiModel.Text -> {
                Text(
                    text = result.text,
                    color = FloconTheme.colors.ink,
                    modifier = Modifier.padding(FloconTheme.spacing.small),
                )
            }

            is QueryResultUiModel.Values -> {
                val color = FloconTheme.colors.line

                var selectedIndex by remember(result.columns) {
                    mutableStateOf<Int?>(null)
                }
                val horizontalScrollState = rememberScrollState()
                val columnsWidth = remember(result.columns) {
                    mutableStateListOf<Dp>().apply { repeat(result.columns.size) { add(DefaultColumnWidth) } }
                }
                val menuLabels = CellMenuLabels(
                    edit = stringResource(Res.string.general_edit),
                    setNull = stringResource(Res.string.database_row_set_null),
                    copy = stringResource(Res.string.general_copy_value),
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colors.bg100)
                        .border(
                            width = 1.dp,
                            color = color,
                            shape = FloconTheme.shapes.medium
                        )
                ) {
                    ResultHeader(
                        result = result,
                        modifier = Modifier.fillMaxWidth(),
                        onExportCsvClicked = { onAction(DatabaseTabAction.ExportCsv) },
                    )
                    FloconHorizontalDivider(
                        color = color,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxSize()) {
                            ResultColumnsHeader(
                                columns = result.columns,
                                columnsWidth = columnsWidth,
                                scrollState = horizontalScrollState,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(horizontalScrollState)
                            ) {
                                itemsIndexed(result.rows) { index, row ->
                                    val selected = index == selectedIndex

                                    DatabaseResultRow(
                                        index = index,
                                        row = row,
                                        columnsWidth = columnsWidth,
                                        selected = selected,
                                        editTarget = result.editTarget,
                                        cellEdit = cellEdit?.takeIf { it.rowIndex == index },
                                        menuLabels = menuLabels,
                                        onAction = onAction,
                                        onSelected = { selectedIndex = index },
                                    )

                                    if (index != result.rows.lastIndex) {
                                        FloconHorizontalDivider(
                                            color = color,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                        ColumnResizeHandles(
                            columnsWidth = columnsWidth,
                            scrollState = horizontalScrollState,
                            onResize = { index, delta ->
                                columnsWidth[index] = (columnsWidth[index] + delta).coerceAtLeast(MinColumnWidth)
                            },
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseResultRow(
    index: Int,
    row: DatabaseRowUiModel,
    columnsWidth: List<Dp>,
    selected: Boolean,
    editTarget: DatabaseRowEditTargetUiModel?,
    /** Only when a cell of this row is edited. */
    cellEdit: DatabaseCellEditUiModel?,
    menuLabels: CellMenuLabels,
    onAction: (DatabaseTabAction) -> Unit,
    onSelected: () -> Unit,
) {
    val isRowEditable = editTarget.canEditRow(row.items)
    Row(
        modifier = Modifier
            .height(FloconTheme.sizes.rowHeight)
            .clip(FloconTheme.shapes.small)
            .then(
                if (selected) {
                    Modifier.background(FloconTheme.colors.accentSoft)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = FloconTheme.spacing.small)
            .clickable(onClick = onSelected),
        verticalAlignment = Alignment.CenterVertically
    ) {
        row.items.forEachIndexed { columnIndex, item ->
            DatabaseResultCell(
                value = item,
                width = columnsWidth.getOrElse(columnIndex) { DefaultColumnWidth },
                isEditable = isRowEditable && editTarget?.canEditColumn(columnIndex) == true,
                cellEdit = cellEdit?.takeIf { it.columnIndex == columnIndex },
                menuLabels = menuLabels,
                onStartEdit = { onAction(DatabaseTabAction.StartCellEdit(rowIndex = index, columnIndex = columnIndex)) },
                onSetNull = { onAction(DatabaseTabAction.SetCellNull(rowIndex = index, columnIndex = columnIndex)) },
                onCopy = { onAction(DatabaseTabAction.CopyCellValue(item ?: "NULL")) },
                onCommit = { onAction(DatabaseTabAction.CommitCellEdit(rowIndex = index, columnIndex = columnIndex, value = it)) },
                onCancel = { onAction(DatabaseTabAction.CancelCellEdit) },
            )
        }
    }
}

@Immutable
private class CellMenuLabels(
    val edit: String,
    val setNull: String,
    val copy: String,
)

/** Double click edits the value, like the database inspector of Android Studio; right click sets it to `NULL`. */
@Composable
private fun DatabaseResultCell(
    value: String?,
    width: Dp,
    isEditable: Boolean,
    /** Only when this cell is edited. */
    cellEdit: DatabaseCellEditUiModel?,
    menuLabels: CellMenuLabels,
    onStartEdit: () -> Unit,
    onSetNull: () -> Unit,
    onCopy: () -> Unit,
    onCommit: (String) -> Unit,
    onCancel: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (cellEdit != null) {
            DatabaseCellTextField(
                initialValue = value.orEmpty(),
                cellEdit = cellEdit,
                onCommit = onCommit,
                onCancel = onCancel,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            val startEdit by rememberUpdatedState(onStartEdit)
            ContextualMenu(
                itemsProvider = {
                    buildList {
                        if (isEditable) {
                            add(FloconContextMenuItem.Item(label = menuLabels.edit, onClick = onStartEdit))
                            if (value != null) {
                                add(FloconContextMenuItem.Item(label = menuLabels.setNull, onClick = onSetNull))
                            }
                        }
                        add(FloconContextMenuItem.Item(label = menuLabels.copy, onClick = onCopy))
                    }.toImmutableList()
                },
            ) {
                Text(
                    text = value ?: "NULL",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = FloconTheme.colors.inkMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isEditable) {
                                // not consumed: the row is selected by the same clicks
                                Modifier.pointerInput(Unit) {
                                    var lastClickUptime = 0L
                                    awaitEachGesture {
                                        awaitFirstDown()
                                        if (!currentEvent.buttons.isPrimaryPressed) return@awaitEachGesture
                                        val up = waitForUpOrCancellation() ?: return@awaitEachGesture
                                        if (up.uptimeMillis - lastClickUptime <= viewConfiguration.doubleTapTimeoutMillis) {
                                            lastClickUptime = 0L
                                            startEdit()
                                        } else {
                                            lastClickUptime = up.uptimeMillis
                                        }
                                    }
                                }
                            } else {
                                Modifier
                            }
                        )
                        .graphicsLayer {
                            if (value == null) {
                                alpha = 0.5f
                            }
                        }
                        .padding(horizontal = FloconTheme.spacing.extraSmall),
                )
            }
        }
    }
}

/** Enter or leaving the field writes the value, Escape cancels. */
@Composable
private fun DatabaseCellTextField(
    initialValue: String,
    cellEdit: DatabaseCellEditUiModel,
    onCommit: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf(TextFieldValue(initialValue, selection = TextRange(0, initialValue.length))) }
    val focusRequester = remember { FocusRequester() }
    // the field leaves the composition when the value is written: it must not be written twice
    var wasFocused by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(cellEdit.isInvalid) {
        if (cellEdit.isInvalid) {
            isDone = false
            focusRequester.requestFocus()
        }
    }

    fun commit() {
        if (isDone) return
        isDone = true
        onCommit(value.text)
    }

    BasicTextField(
        value = value,
        onValueChange = { value = it },
        singleLine = true,
        readOnly = cellEdit.isSaving,
        textStyle = FloconTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            color = FloconTheme.colors.ink,
        ),
        cursorBrush = SolidColor(FloconTheme.colors.accent),
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    wasFocused = true
                } else if (wasFocused) {
                    commit()
                }
            }
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.Enter, Key.NumPadEnter -> {
                        commit()
                        true
                    }

                    Key.Escape -> {
                        isDone = true
                        onCancel()
                        true
                    }

                    else -> false
                }
            }
            .background(FloconTheme.colors.bg000, FloconTheme.shapes.small)
            .border(
                width = 1.dp,
                color = if (cellEdit.isInvalid) FloconTheme.colors.statusError else FloconTheme.colors.accent,
                shape = FloconTheme.shapes.small,
            )
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = 2.dp),
    )
}

@Composable
private fun ResultColumnsHeader(
    columns: List<String>,
    columnsWidth: List<Dp>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .floconTableHeader()
            .horizontalScroll(scrollState)
            .padding(horizontal = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        columns.fastForEachIndexed { index, column ->
            FloconTableHeaderLabel(
                text = column,
                uppercase = false,
                modifier = Modifier
                    .width(columnsWidth.getOrElse(index) { DefaultColumnWidth })
                    .padding(all = FloconTheme.spacing.extraSmall),
            )
        }
    }
}

/**
 * Resize handles drawn over the whole table (header and rows), one at the right edge of each
 * column, following the horizontal scroll.
 */
@Composable
private fun ColumnResizeHandles(
    columnsWidth: List<Dp>,
    scrollState: ScrollState,
    onResize: (index: Int, delta: Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val startPadding = FloconTheme.spacing.small
    Box(modifier.clipToBounds()) {
        var columnEnd = startPadding
        columnsWidth.forEachIndexed { index, width ->
            columnEnd += width
            val handleStart = columnEnd - HandleWidth / 2
            FloconColumnResizeHandle(
                onResize = { delta -> onResize(index, delta) },
                modifier = Modifier.offset { IntOffset(handleStart.roundToPx() - scrollState.value, 0) },
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
            .floconTableHeaderBackground()
            .padding(horizontal = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(Res.string.nb_lines, result.rows.size),
            style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .padding(all = FloconTheme.spacing.extraSmall),
        )

        Spacer(Modifier.weight(1f))

        Box(
            Modifier.size(30.dp).clickable {
                onExportCsvClicked()
            }.padding(FloconTheme.spacing.compact)
        ) {
            Image(
                imageVector = Icons.AutoMirrored.Outlined.DriveFileMove,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(FloconTheme.colors.ink),
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
        DatabaseResult(
            result = result,
            cellEdit = null,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewValues() {
    val result = QueryResultUiModel.Values(
        columns = listOf("ID", "Name", "Email"),
        rows = persistentListOf(
            DatabaseRowUiModel(items = listOf("1", "John Doe", "john.doe@example.com")),
            DatabaseRowUiModel(items = listOf("2", "Jane Smith", "jane.smith@example.com")),
            DatabaseRowUiModel(items = listOf("3", "Peter Jones", null)),
        ),
    )
    FloconTheme {
        DatabaseResult(
            result = result,
            cellEdit = null,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun DatabaseResultViewPreviewNull() {
    FloconTheme {
        DatabaseResult(
            result = null,
            cellEdit = null,
            onAction = {},
        )
    }
}

package io.github.openflocon.flocondesktop.features.files.view.header

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_column_modified
import flocondesktop.composeapp.generated.resources.name
import flocondesktop.composeapp.generated.resources.size
import io.github.openflocon.flocondesktop.features.files.model.FileColumnUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.isSorted
import io.github.openflocon.flocondesktop.features.files.model.previewFilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconTableHeaderLabel
import io.github.openflocon.library.designsystem.components.floconTableHeader
import io.github.openflocon.library.designsystem.components.floconTableHeaderTextColor
import org.jetbrains.compose.resources.stringResource

// the type tile of a row, not scaled (like the icons)
internal val FileTileSize = 32.dp
private val SortArrowSize = 12.dp

/** The grid shared by the header and the rows of the list: [checkbox] | tile | name | modified | size | trailing. */
internal object FilesListColumns {
    val checkbox: Dp
        @Composable @ReadOnlyComposable
        get() = FloconTheme.sizes.columnActions

    val tile: Dp
        @Composable @ReadOnlyComposable
        get() = FileTileSize + FloconTheme.spacing.medium

    val modified: Dp
        @Composable @ReadOnlyComposable
        get() = FloconTheme.sizes.columnExtraLarge

    val size: Dp
        @Composable @ReadOnlyComposable
        get() = FloconTheme.sizes.columnExtraLarge + FloconTheme.spacing.doubleExtraLarge

    val trailing: Dp
        @Composable @ReadOnlyComposable
        get() = FloconTheme.sizes.columnActions + FloconTheme.spacing.small

    val horizontalPadding: Dp
        @Composable @ReadOnlyComposable
        get() = FloconTheme.spacing.medium
}

/** Line drawn at the bottom of the header and of each row. */
@Composable
internal fun Modifier.filesBottomLine(): Modifier {
    val color = FloconTheme.colors.line
    return drawBehind {
        val y = size.height - 0.5.dp.toPx()
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
    }
}

@Composable
internal fun FilesListHeader(
    state: FilesHeaderStateUiModel,
    isSelecting: Boolean,
    selectingAll: Boolean,
    onSelectAll: (selectingAll: Boolean) -> Unit,
    clickOnSort: (FileColumnUiModel, SortedByUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .floconTableHeader(FloconTheme.sizes.headerHeight + FloconTheme.spacing.small)
            .filesBottomLine()
            .padding(horizontal = FilesListColumns.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSelecting) {
            Box(modifier = Modifier.width(FilesListColumns.checkbox), contentAlignment = Alignment.CenterStart) {
                FloconCheckbox(
                    checked = selectingAll,
                    onCheckedChange = { onSelectAll(!selectingAll) },
                )
            }
        }
        Box(Modifier.width(FilesListColumns.tile))
        HeaderCell(
            title = stringResource(Res.string.name),
            sortedBy = state.isSorted(FileColumnUiModel.Name),
            onClick = { clickOnSort(FileColumnUiModel.Name, it) },
            modifier = Modifier.weight(1f),
        )
        HeaderCell(
            title = stringResource(Res.string.files_column_modified),
            sortedBy = state.isSorted(FileColumnUiModel.Date),
            onClick = { clickOnSort(FileColumnUiModel.Date, it) },
            modifier = Modifier.width(FilesListColumns.modified),
        )
        HeaderCell(
            title = stringResource(Res.string.size),
            sortedBy = state.isSorted(FileColumnUiModel.Size),
            onClick = { clickOnSort(FileColumnUiModel.Size, it) },
            modifier = Modifier.width(FilesListColumns.size),
        )
        Box(Modifier.width(FilesListColumns.trailing))
    }
}

/** Next sort when clicking a column: none -> ascending -> descending -> none. */
internal fun SortedByUiModel.next(): SortedByUiModel = when (this) {
    SortedByUiModel.None -> SortedByUiModel.Enabled.Ascending
    SortedByUiModel.Enabled.Ascending -> SortedByUiModel.Enabled.Descending
    SortedByUiModel.Enabled.Descending -> SortedByUiModel.None
}

@Composable
private fun HeaderCell(
    title: String,
    sortedBy: SortedByUiModel,
    onClick: (SortedByUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSorted = sortedBy != SortedByUiModel.None
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .clickable { onClick(sortedBy.next()) }
                .pointerHoverIcon(PointerIcon.Hand),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        ) {
            HeaderLabel(title = title, highlighted = isSorted)
            SortArrow(sortedBy)
        }
    }
}

@Composable
private fun HeaderLabel(title: String, highlighted: Boolean) {
    FloconTableHeaderLabel(
        text = title,
        color = if (highlighted) FloconTheme.colors.ink else floconTableHeaderTextColor,
    )
}

@Composable
internal fun SortArrow(sortedBy: SortedByUiModel, modifier: Modifier = Modifier) {
    val icon = when (sortedBy) {
        SortedByUiModel.Enabled.Ascending -> Icons.Outlined.KeyboardArrowUp
        SortedByUiModel.Enabled.Descending -> Icons.Outlined.KeyboardArrowDown
        SortedByUiModel.None -> return
    }
    FloconIcon(
        imageVector = icon,
        tint = floconTableHeaderTextColor,
        modifier = modifier.size(SortArrowSize),
    )
}

@Preview
@Composable
private fun FilesListHeaderPreview() {
    FloconTheme {
        FilesListHeader(
            state = previewFilesHeaderStateUiModel().copy(
                sortedBy = FilesHeaderStateUiModel.SortedBy(FileColumnUiModel.Name, SortedByUiModel.Enabled.Ascending),
            ),
            isSelecting = false,
            selectingAll = false,
            onSelectAll = {},
            clickOnSort = { _, _ -> },
        )
    }
}

package io.github.openflocon.flocondesktop.features.files.view.header

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_column_modified
import flocondesktop.composeapp.generated.resources.files_sort_by
import flocondesktop.composeapp.generated.resources.files_sort_default
import flocondesktop.composeapp.generated.resources.name
import flocondesktop.composeapp.generated.resources.size
import io.github.openflocon.flocondesktop.features.files.model.FileColumnUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesHeaderStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import org.jetbrains.compose.resources.stringResource

private val SortButtonRadius = 6.dp

@Composable
private fun columnLabel(column: FileColumnUiModel): String = when (column) {
    FileColumnUiModel.Name -> stringResource(Res.string.name)
    FileColumnUiModel.Date -> stringResource(Res.string.files_column_modified)
    FileColumnUiModel.Size -> stringResource(Res.string.size)
}

/**
 * "Sort by [Name ▲]" row above the grid. Picking the sorted column again reverses its order,
 * picking another one sorts it ascending, "Default" keeps the order of the device.
 */
@Composable
internal fun FilesGridSortBar(
    state: FilesHeaderStateUiModel,
    clickOnSort: (FileColumnUiModel, SortedByUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val sortedBy = state.sortedBy

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FloconTheme.sizes.headerHeight + FloconTheme.spacing.small)
            .filesBottomLine()
            .padding(horizontal = FloconTheme.spacing.large + FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = stringResource(Res.string.files_sort_by),
            style = FloconTheme.typography.labelMedium,
            color = FloconTheme.colors.inkMuted,
        )
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(SortButtonRadius))
                    .border(1.dp, FloconTheme.colors.line, RoundedCornerShape(SortButtonRadius))
                    .clickable { expanded = true }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            ) {
                Text(
                    text = sortedBy?.let { columnLabel(it.column) } ?: stringResource(Res.string.files_sort_default),
                    style = FloconTheme.typography.labelMedium,
                    color = FloconTheme.colors.ink,
                )
                SortArrow(sortedBy?.sortedBy ?: SortedByUiModel.None)
            }
            FloconDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                FloconDropdownMenuItem(
                    text = stringResource(Res.string.files_sort_default),
                    trailingIcon = if (sortedBy == null) Icons.Outlined.Check else null,
                    onClick = {
                        expanded = false
                        clickOnSort(FileColumnUiModel.Name, SortedByUiModel.None)
                    },
                )
                FileColumnUiModel.entries.forEach { column ->
                    val isCurrent = sortedBy?.column == column
                    FloconDropdownMenuItem(
                        text = columnLabel(column),
                        trailingIcon = if (isCurrent) Icons.Outlined.Check else null,
                        onClick = {
                            expanded = false
                            val next = if (isCurrent && sortedBy?.sortedBy == SortedByUiModel.Enabled.Ascending) {
                                SortedByUiModel.Enabled.Descending
                            } else {
                                SortedByUiModel.Enabled.Ascending
                            }
                            clickOnSort(column, next)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FilesGridSortBarPreview() {
    FloconTheme {
        FilesGridSortBar(
            state = previewFilesHeaderStateUiModel().copy(
                sortedBy = FilesHeaderStateUiModel.SortedBy(FileColumnUiModel.Name, SortedByUiModel.Enabled.Ascending),
            ),
            clickOnSort = { _, _ -> },
        )
    }
}

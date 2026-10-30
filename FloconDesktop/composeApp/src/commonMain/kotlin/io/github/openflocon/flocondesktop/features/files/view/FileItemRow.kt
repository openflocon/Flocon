@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_actions
import flocondesktop.composeapp.generated.resources.files_size_unknown
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.files.model.FilePathUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFileUiModels
import io.github.openflocon.flocondesktop.features.files.view.header.FileTileSize
import io.github.openflocon.flocondesktop.features.files.view.header.FilesListColumns
import io.github.openflocon.flocondesktop.features.files.view.header.filesBottomLine
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip
import org.jetbrains.compose.resources.stringResource

private val TileRadius = 8.dp
private val TileIconSize = 18.dp
private val SizeBarWidth = 110.dp
private val SizeBarHeight = 4.dp
private val ActionButtonSize = 30.dp
private val ActionIconSize = 14.dp
private val ChevronSize = 16.dp
private const val HoverAlpha = 0.04f

@Composable
fun FileItemRow(
    file: FileUiModel,
    index: Int,
    multiSelect: Boolean,
    multiSelected: Boolean,
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    var actionsExpanded by remember { mutableStateOf(false) }
    val colors = FloconTheme.colors

    ContextualMenu(
        items = rememberFileContextMenuItems(file, onContextualAction),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(FloconTheme.sizes.rowHeight + FloconTheme.spacing.medium + FloconTheme.spacing.small)
                .background(
                    when {
                        multiSelected -> colors.accentSoft
                        hovered || actionsExpanded -> colors.ink.copy(alpha = HoverAlpha)
                        else -> Color.Transparent
                    },
                )
                .filesBottomLine()
                .hoverable(interactionSource)
                .fileClickable(
                    file = file,
                    index = index,
                    multiSelect = multiSelect,
                    multiSelected = multiSelected,
                    interactionSource = interactionSource,
                    onClick = onClick,
                    onSelectFile = onSelectFile,
                )
                .padding(horizontal = FilesListColumns.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (multiSelect) {
                Box(Modifier.width(FilesListColumns.checkbox), contentAlignment = Alignment.CenterStart) {
                    FloconCheckbox(
                        checked = multiSelected,
                        onCheckedChange = { selected ->
                            val path = (file.path as? FilePathUiModel.Real)?.absolutePath
                            if (path != null) {
                                onSelectFile(path, selected, index, false)
                            }
                        },
                    )
                }
            }

            Box(Modifier.width(FilesListColumns.tile), contentAlignment = Alignment.CenterStart) {
                FileTypeTile(type = file.type)
            }

            Text(
                text = file.name,
                style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = FloconTheme.spacing.medium),
            )

            Text(
                text = file.dateFormatted.orEmpty(),
                style = FloconTheme.typography.bodySmall,
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(FilesListColumns.modified),
            )

            FileSizeCell(
                sizeFormatted = file.sizeFormatted,
                sizeRatio = file.sizeRatio,
                modifier = Modifier.width(FilesListColumns.size),
            )

            Box(
                modifier = Modifier.width(FilesListColumns.trailing),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (file.isDirectory) {
                    FloconIcon(
                        imageVector = Icons.Outlined.ChevronRight,
                        tint = colors.inkFaint,
                        modifier = Modifier.size(ChevronSize),
                    )
                } else if (hovered || actionsExpanded) {
                    Box {
                        RowActionsButton(onClick = { actionsExpanded = true })
                        FileActionsMenu(
                            file = file,
                            expanded = actionsExpanded,
                            onDismissRequest = { actionsExpanded = false },
                            onContextualAction = onContextualAction,
                        )
                    }
                }
            }
        }
    }
}

/** Rounded tile of the type of an entry: the icon of its family, tinted with the ink of the type on a soft background. */
@Composable
internal fun FileTypeTile(
    type: FileTypeUiModel,
    modifier: Modifier = Modifier,
) {
    val typeColors = filesPalette.of(type)
    Box(
        modifier = modifier
            .size(FileTileSize)
            .clip(RoundedCornerShape(TileRadius))
            .background(typeColors.tile),
        contentAlignment = Alignment.Center,
    ) {
        FloconIcon(
            imageVector = type.icon(),
            tint = typeColors.ink,
            modifier = Modifier.size(TileIconSize),
        )
    }
}

/** Size bar (relative to the biggest entry of the folder) and the size, or a dash when the size is unknown. */
@Composable
private fun FileSizeCell(
    sizeFormatted: String?,
    sizeRatio: Float?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        if (sizeFormatted == null) {
            Text(
                text = stringResource(Res.string.files_size_unknown),
                style = FloconTheme.typography.monoMedium,
                color = colors.inkFaint,
            )
        } else {
            if (sizeRatio != null) {
                Box(
                    modifier = Modifier
                        .width(SizeBarWidth)
                        .height(SizeBarHeight)
                        .clip(RoundedCornerShape(SizeBarHeight / 2))
                        .background(colors.line),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(sizeRatio)
                            .height(SizeBarHeight)
                            .clip(RoundedCornerShape(SizeBarHeight / 2))
                            .background(filesPalette.sizeBar),
                    )
                }
            }
            Text(
                text = sizeFormatted,
                style = FloconTheme.typography.monoMedium,
                color = colors.ink.copy(alpha = 0.85f),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RowActionsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WithTooltip(stringResource(Res.string.files_actions)) {
        Box(
            modifier = modifier
                .size(ActionButtonSize)
                .clip(RoundedCornerShape(6.dp))
                .background(FloconTheme.colors.bg300)
                .clickable(onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
            contentAlignment = Alignment.Center,
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.MoreHoriz,
                tint = FloconTheme.colors.ink,
                modifier = Modifier.size(ActionIconSize),
            )
        }
    }
}

@Preview
@Composable
private fun FileItemRowPreview() {
    FloconTheme {
        Column(Modifier.background(FloconTheme.colors.bg100)) {
            previewFileUiModels().forEachIndexed { index, file ->
                FileItemRow(
                    file = file,
                    index = index,
                    multiSelect = false,
                    multiSelected = false,
                    onClick = {},
                    onContextualAction = { _, _ -> },
                    onSelectFile = { _, _, _, _ -> },
                )
            }
        }
    }
}

@Preview
@Composable
private fun FileItemRowMultiSelectPreview() {
    FloconTheme {
        Column(Modifier.background(FloconTheme.colors.bg100)) {
            previewFileUiModels().take(3).forEachIndexed { index, file ->
                FileItemRow(
                    file = file,
                    index = index,
                    multiSelect = true,
                    multiSelected = index == 2,
                    onClick = {},
                    onContextualAction = { _, _ -> },
                    onSelectFile = { _, _, _, _ -> },
                )
            }
        }
    }
}

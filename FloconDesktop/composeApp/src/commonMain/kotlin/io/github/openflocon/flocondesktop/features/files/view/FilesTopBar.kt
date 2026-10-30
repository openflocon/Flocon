@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownPanelAnchor
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_doc_open_documentation
import flocondesktop.composeapp.generated.resources.files_back
import flocondesktop.composeapp.generated.resources.files_breadcrumb_root
import flocondesktop.composeapp.generated.resources.files_breadcrumb_separator
import flocondesktop.composeapp.generated.resources.files_delete_selection
import flocondesktop.composeapp.generated.resources.files_exit_selection
import flocondesktop.composeapp.generated.resources.files_filter
import flocondesktop.composeapp.generated.resources.files_folder_sizes
import flocondesktop.composeapp.generated.resources.files_folder_sizes_tooltip
import flocondesktop.composeapp.generated.resources.files_more_actions
import flocondesktop.composeapp.generated.resources.files_refresh
import flocondesktop.composeapp.generated.resources.files_select_multiple
import flocondesktop.composeapp.generated.resources.files_selected_count
import flocondesktop.composeapp.generated.resources.files_view_grid
import flocondesktop.composeapp.generated.resources.files_view_list
import flocondesktop.composeapp.generated.resources.general_delete_content
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesAction
import io.github.openflocon.flocondesktop.features.files.model.FilesStateUiModel
import io.github.openflocon.flocondesktop.features.files.model.FilesViewModeUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.WithTooltip
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private val ControlRadius = 8.dp
private val SegmentRadius = 6.dp
private val ControlIconSize = 15.dp
private val SegmentPadding = 2.dp
private val SwitchWidth = 26.dp
private val SwitchHeight = 14.dp
private val SwitchKnob = 10.dp
private val BreadcrumbHomeIconSize = 14.dp
private val BreadcrumbSegmentMaxWidth = 180.dp

/**
 * Bar of the Files screen: back button and clickable breadcrumb, filter, list / grid toggle, "Folder sizes" toggle,
 * refresh and an overflow menu with the secondary actions (multi-selection, delete content, documentation).
 */
@Composable
fun FilesTopBar(
    state: FilesStateUiModel,
    filterText: State<String>,
    onAction: (FilesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = state.current
    FloconPageTopBar(
        modifier = modifier,
        filterBar = {
            if (current != null) {
                FloconIconButton(
                    imageVector = Icons.Outlined.ChevronLeft,
                    tooltip = stringResource(Res.string.files_back),
                    onClick = { onAction(FilesAction.NavigateUp) },
                )
            }
            FilesBreadcrumb(
                backStack = state.backStack,
                current = current,
                onRootClicked = { onAction(FilesAction.NavigateToRoot) },
                onBackStackEntryClicked = { onAction(FilesAction.NavigateToBackStackEntry(it)) },
                modifier = Modifier.weight(1f),
            )
        },
        actions = {
            FilterBar(
                filterText = filterText,
                placeholderText = stringResource(Res.string.files_filter),
                onTextChange = { onAction(FilesAction.FilterTextChanged(it)) },
                // same width as a side panel
                modifier = Modifier.width(FloconTheme.sizes.sidePanelWidth),
            )
            FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
            ViewModeToggle(
                viewMode = state.viewMode,
                onViewModeChange = { onAction(FilesAction.ChangeViewMode(it)) },
            )
            FolderSizesToggle(
                checked = state.options.withFoldersSize,
                onCheckedChange = { onAction(FilesAction.UpdateWithFoldersSize(it)) },
            )
            AnimatedVisibility(state.isSelecting) {
                SelectionActions(
                    selectedCount = state.multiSelectedPaths.size,
                    onDeleteSelection = { onAction(FilesAction.DeleteSelection) },
                    onClearMultiSelect = { onAction(FilesAction.ClearMultiSelect) },
                )
            }
            RefreshButton(onClick = { onAction(FilesAction.Refresh) })
            FilesOverflowMenu(
                canSelect = state.canSelect && !state.isSelecting,
                canDeleteContent = current?.isDirectory == true,
                onMultiSelect = { onAction(FilesAction.MultiSelect) },
                onDeleteContent = { onAction(FilesAction.DeleteContent) },
            )
        },
    )
}

/** Home / Files / datastore: every segment but the current one navigates back to its folder. */
@Composable
private fun FilesBreadcrumb(
    backStack: ImmutableList<FileUiModel>,
    current: FileUiModel?,
    onRootClicked: () -> Unit,
    onBackStackEntryClicked: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        BreadcrumbSegment(
            text = stringResource(Res.string.files_breadcrumb_root),
            icon = Icons.Outlined.Home,
            isCurrent = current == null,
            onClick = onRootClicked,
        )
        backStack.forEachIndexed { index, file ->
            BreadcrumbSeparator()
            BreadcrumbSegment(
                text = file.name,
                isCurrent = false,
                onClick = { onBackStackEntryClicked(index) },
            )
        }
        if (current != null) {
            BreadcrumbSeparator()
            BreadcrumbSegment(
                text = current.name,
                isCurrent = true,
                onClick = {},
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun BreadcrumbSeparator() {
    Text(
        text = stringResource(Res.string.files_breadcrumb_separator),
        style = FloconTheme.typography.bodyMedium,
        color = FloconTheme.colors.inkFaint,
    )
}

@Composable
private fun BreadcrumbSegment(
    text: String,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val colors = FloconTheme.colors
    val contentColor = if (isCurrent) colors.ink else colors.inkMuted
    Row(
        modifier = modifier
            .widthIn(max = if (isCurrent) Dp.Unspecified else BreadcrumbSegmentMaxWidth)
            .clip(RoundedCornerShape(ControlRadius))
            .background(if (isCurrent) colors.bg300 else Color.Transparent)
            .then(
                if (isCurrent) {
                    Modifier
                } else {
                    Modifier
                        .clickable(onClick = onClick)
                        .pointerHoverIcon(PointerIcon.Hand)
                },
            )
            .padding(horizontal = FloconTheme.spacing.small + FloconTheme.spacing.tiny, vertical = FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        if (icon != null) {
            FloconIcon(
                imageVector = icon,
                tint = contentColor,
                modifier = Modifier.size(BreadcrumbHomeIconSize),
            )
        }
        Text(
            text = text,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
            ),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Segmented [list | grid] buttons. */
@Composable
private fun ViewModeToggle(
    viewMode: FilesViewModeUiModel,
    onViewModeChange: (FilesViewModeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(RoundedCornerShape(ControlRadius))
            .background(FloconTheme.colors.bg200)
            .border(1.dp, FloconTheme.colors.line, RoundedCornerShape(ControlRadius))
            .padding(SegmentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ViewModeSegment(
            icon = Icons.AutoMirrored.Outlined.List,
            tooltip = stringResource(Res.string.files_view_list),
            selected = viewMode == FilesViewModeUiModel.List,
            onClick = { onViewModeChange(FilesViewModeUiModel.List) },
        )
        ViewModeSegment(
            icon = Icons.Outlined.GridView,
            tooltip = stringResource(Res.string.files_view_grid),
            selected = viewMode == FilesViewModeUiModel.Grid,
            onClick = { onViewModeChange(FilesViewModeUiModel.Grid) },
        )
    }
}

@Composable
private fun ViewModeSegment(
    icon: ImageVector,
    tooltip: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    WithTooltip(tooltip) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(FloconTheme.sizes.controlHeight)
                .clip(RoundedCornerShape(SegmentRadius))
                .background(if (selected) FloconTheme.colors.bg300 else Color.Transparent)
                .clickable(onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
            contentAlignment = Alignment.Center,
        ) {
            FloconIcon(
                imageVector = icon,
                tint = if (selected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
                modifier = Modifier.size(ControlIconSize),
            )
        }
    }
}

/** Optional (and costly on the device) computation of the size of the folders: small switch + label. */
@Composable
private fun FolderSizesToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    WithTooltip(stringResource(Res.string.files_folder_sizes_tooltip)) {
        BarButton(
            onClick = { onCheckedChange(!checked) },
            containerColor = if (checked) colors.bg300 else Color.Transparent,
            borderColor = if (checked) colors.lineStrong else colors.line,
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier
                    .size(width = SwitchWidth, height = SwitchHeight)
                    .clip(CircleShape)
                    .background(if (checked) filesPalette.switchTrackOn else colors.lineStrong),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = if (checked) SwitchWidth - SwitchKnob - SegmentPadding else SegmentPadding)
                        .size(SwitchKnob)
                        .background(colors.ink, CircleShape),
                )
            }
            Text(
                text = stringResource(Res.string.files_folder_sizes),
                style = FloconTheme.typography.bodyMedium,
                color = colors.ink,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RefreshButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BarButton(onClick = onClick, modifier = modifier) {
        FloconIcon(
            imageVector = Icons.Outlined.Refresh,
            tint = FloconTheme.colors.ink,
            modifier = Modifier.size(ControlIconSize),
        )
        Text(
            text = stringResource(Res.string.files_refresh),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
            maxLines = 1,
        )
    }
}

/** Bordered button of the bar, icon and / or label. */
@Composable
private fun BarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    borderColor: Color = FloconTheme.colors.line,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(RoundedCornerShape(ControlRadius))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(ControlRadius))
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        content = content,
    )
}

/** While selecting: "N selected" with the delete button, and the button leaving the selection mode. */
@Composable
private fun SelectionActions(
    selectedCount: Int,
    onDeleteSelection: () -> Unit,
    onClearMultiSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        AnimatedVisibility(selectedCount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(FloconTheme.sizes.pageBarControlHeight)
                    .background(color = FloconTheme.colors.accentSoft, shape = RoundedCornerShape(ControlRadius))
                    .padding(start = FloconTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(Res.string.files_selected_count, selectedCount),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.accent,
                    maxLines = 1,
                )
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    tooltip = stringResource(Res.string.files_delete_selection),
                    onClick = onDeleteSelection,
                )
            }
        }
        FloconIconButton(
            imageVector = Icons.Outlined.Close,
            tooltip = stringResource(Res.string.files_exit_selection),
            onClick = onClearMultiSelect,
        )
    }
}

@Composable
private fun FilesOverflowMenu(
    canSelect: Boolean,
    canDeleteContent: Boolean,
    onMultiSelect: () -> Unit,
    onDeleteContent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    FloconDropdownMenu(
        expanded = expanded,
        anchor = DropdownPanelAnchor.BottomEnd,
        onExpandRequest = { expanded = true },
        onDismissRequest = { expanded = false },
        modifier = modifier,
        anchorContent = {
            WithTooltip(stringResource(Res.string.files_more_actions)) {
                Box(
                    modifier = Modifier
                        .size(FloconTheme.sizes.pageBarControlHeight)
                        .clip(RoundedCornerShape(ControlRadius))
                        .border(1.dp, FloconTheme.colors.line, RoundedCornerShape(ControlRadius))
                        .clickable { expanded = true }
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentAlignment = Alignment.Center,
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        tint = FloconTheme.colors.ink,
                        modifier = Modifier.size(ControlIconSize),
                    )
                }
            }
        },
    ) {
        if (canSelect) {
            FloconDropdownMenuItem(
                text = stringResource(Res.string.files_select_multiple),
                leadingIcon = Icons.Outlined.CheckBox,
                onClick = {
                    expanded = false
                    onMultiSelect()
                },
            )
        }
        if (canDeleteContent) {
            FloconDropdownMenuItem(
                text = stringResource(Res.string.general_delete_content),
                leadingIcon = Icons.Outlined.DeleteSweep,
                onClick = {
                    expanded = false
                    onDeleteContent()
                },
            )
        }
        FloconDropdownMenuItem(
            text = stringResource(Res.string.feature_doc_open_documentation),
            leadingIcon = Icons.Outlined.Info,
            onClick = {
                expanded = false
                runCatching { uriHandler.openUri(FeatureDoc.Files.url) }
            },
        )
    }
}

@Preview
@Composable
private fun FilesTopBarPreview() {
    FloconTheme {
        FilesTopBar(
            state = previewFilesStateUiModel(),
            filterText = remember { mutableStateOf("") },
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun FilesTopBarRootPreview() {
    FloconTheme {
        FilesTopBar(
            state = previewFilesStateUiModel().copy(current = null, backStack = persistentListOf()),
            filterText = remember { mutableStateOf("") },
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

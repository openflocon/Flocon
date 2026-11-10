package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.ContextualMenu
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.flocondesktop.features.files.model.FileUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFileUiModels
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon

// the drawings are not scaled, like the icons
internal val FileGridCellMinWidth = 120.dp
private val IconAreaWidth = 96.dp
private val IconAreaHeight = 84.dp
private val IconAreaRadius = 8.dp
private val FolderWidth = 76.dp
private val FolderHeight = 62.dp
private val PageWidth = 54.dp
private val PageHeight = 70.dp
private val PageIconSize = 22.dp
private val PageIconTop = 20.dp
private val BadgeBottom = 8.dp
private val BadgeRadius = 4.dp
private val BadgeTextSize = 9.sp
private val NameRadius = 5.dp

/** A Finder like grid item: a drawing of the folder / page of the file, the name below and its size. */
@Composable
fun FileGridItem(
    file: FileUiModel,
    index: Int,
    multiSelect: Boolean,
    multiSelected: Boolean,
    showSize: Boolean,
    onClick: (FileUiModel) -> Unit,
    onContextualAction: (FileUiModel, FileUiModel.ContextualAction.Action) -> Unit,
    onSelectFile: (path: String, selected: Boolean, index: Int, shiftHeld: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val highlighted = hovered || multiSelected
    val palette = filesPalette

    ContextualMenu(
        items = rememberFileContextMenuItems(file, onContextualAction),
    ) {
        Column(
            modifier = modifier
                .hoverable(interactionSource)
                .fileClickable(
                    file = file,
                    index = index,
                    multiSelect = multiSelect,
                    multiSelected = multiSelected,
                    interactionSource = interactionSource,
                    onClick = onClick,
                    onSelectFile = onSelectFile,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .size(width = IconAreaWidth, height = IconAreaHeight)
                    .clip(RoundedCornerShape(IconAreaRadius))
                    .background(if (highlighted) FloconTheme.colors.bg300 else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                if (file.isDirectory) {
                    FolderIllustration()
                } else {
                    FileIllustration(type = file.type, extensionLabel = file.extensionLabel)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            ) {
                Text(
                    text = file.name,
                    style = FloconTheme.typography.bodySmall,
                    color = if (highlighted) palette.onSelection else FloconTheme.colors.ink,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(max = FileGridCellMinWidth)
                        .clip(RoundedCornerShape(NameRadius))
                        .background(if (highlighted) palette.selection else Color.Transparent)
                        .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
                )
                val size = file.sizeFormatted
                if (showSize && size != null) {
                    Text(
                        text = size,
                        style = FloconTheme.typography.monoSmall,
                        color = FloconTheme.colors.inkFaint,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** A folder drawing: back tab, front and the highlight of its top edge. */
@Composable
internal fun FolderIllustration(modifier: Modifier = Modifier) {
    val palette = filesPalette
    val vector = remember(palette) {
        ImageVector.Builder(
            defaultWidth = FolderWidth,
            defaultHeight = FolderHeight,
            viewportWidth = 76f,
            viewportHeight = 62f,
        )
            .addPath(
                pathData = addPathNodes("M2 8a5 5 0 0 1 5-5h18l6 6h38a5 5 0 0 1 5 5v40a5 5 0 0 1-5 5H7a5 5 0 0 1-5-5z"),
                fill = SolidColor(palette.folderBack),
            )
            .addPath(
                pathData = addPathNodes("M2 18a5 5 0 0 1 5-5h62a5 5 0 0 1 5 5v36a5 5 0 0 1-5 5H7a5 5 0 0 1-5-5z"),
                fill = SolidColor(palette.folderFront),
            )
            .addPath(
                pathData = addPathNodes("M2 18a5 5 0 0 1 5-5h62a5 5 0 0 1 5 5v2H2z"),
                fill = SolidColor(palette.folderHighlight),
            )
            .build()
    }
    Image(
        painter = rememberVectorPainter(vector),
        contentDescription = null,
        modifier = modifier.size(width = FolderWidth, height = FolderHeight),
    )
}

/** A page with a folded corner and the icon of the family of the file, the extension of the file in a badge colored by its type. */
@Composable
internal fun FileIllustration(
    type: FileTypeUiModel,
    extensionLabel: String,
    modifier: Modifier = Modifier,
) {
    val palette = filesPalette
    val typeColors = palette.of(type)
    val vector = remember(palette) {
        ImageVector.Builder(
            defaultWidth = PageWidth,
            defaultHeight = PageHeight,
            viewportWidth = 54f,
            viewportHeight = 70f,
        )
            .addPath(
                pathData = addPathNodes("M3 4a3 3 0 0 1 3-3h30l15 15v49a3 3 0 0 1-3 3H6a3 3 0 0 1-3-3z"),
                fill = SolidColor(palette.page),
            )
            .addPath(
                pathData = addPathNodes("M36 1v12a3 3 0 0 0 3 3h12z"),
                fill = SolidColor(palette.pageFold),
            )
            .build()
    }
    Box(
        modifier = modifier.size(width = PageWidth, height = PageHeight),
    ) {
        Image(
            painter = rememberVectorPainter(vector),
            contentDescription = null,
            modifier = Modifier.size(width = PageWidth, height = PageHeight),
        )
        FloconIcon(
            imageVector = type.icon(),
            tint = typeColors.deep,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = PageIconTop)
                .size(PageIconSize),
        )
        Text(
            text = extensionLabel,
            // inside a drawing that is not scaled: neither is its text
            style = FloconTheme.typography.monoSmall.copy(fontSize = BadgeTextSize, fontWeight = FontWeight.SemiBold),
            color = palette.onBadge,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BadgeBottom)
                .clip(RoundedCornerShape(BadgeRadius))
                .background(typeColors.ink)
                .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
        )
    }
}

@Preview
@Composable
private fun FileGridItemPreview() {
    FloconTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.extraLarge),
        ) {
            previewFileUiModels().forEachIndexed { index, file ->
                FileGridItem(
                    file = file,
                    index = index,
                    multiSelect = false,
                    multiSelected = index == 2,
                    showSize = !file.isDirectory,
                    onClick = {},
                    onContextualAction = { _, _ -> },
                    onSelectFile = { _, _, _, _ -> },
                    modifier = Modifier.widthIn(min = FileGridCellMinWidth),
                )
            }
        }
    }
}

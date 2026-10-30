@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_footer_copy_path
import flocondesktop.composeapp.generated.resources.files_footer_counts
import flocondesktop.composeapp.generated.resources.files_footer_folders
import flocondesktop.composeapp.generated.resources.files_footer_items
import flocondesktop.composeapp.generated.resources.files_footer_total
import io.github.openflocon.flocondesktop.features.files.model.FilesFooterUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val DotSize = 3.dp
private val PathRadius = 6.dp
private val PathIconSize = 12.dp

/** "N items · M folders • Total 48.6 KB ... /data/user/0/app/files" */
@Composable
internal fun FilesFooter(
    footer: FilesFooterUiModel,
    totalSizeFormatted: String?,
    onCopyPath: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val lineColor = colors.line
    val textStyle = FloconTheme.typography.labelMedium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FloconTheme.sizes.headerHeight + FloconTheme.spacing.small)
            .drawBehind {
                drawLine(lineColor, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            }
            .padding(horizontal = FloconTheme.spacing.large + FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        Text(
            text = stringResource(
                Res.string.files_footer_counts,
                pluralStringResource(Res.plurals.files_footer_items, footer.itemCount, footer.itemCount),
                pluralStringResource(Res.plurals.files_footer_folders, footer.folderCount, footer.folderCount),
            ),
            style = textStyle,
            color = colors.inkMuted,
            maxLines = 1,
        )
        if (totalSizeFormatted != null) {
            Box(
                Modifier
                    .size(DotSize)
                    .background(colors.inkFaint, CircleShape),
            )
            val total = stringResource(Res.string.files_footer_total)
            val monoStyle = FloconTheme.typography.monoMedium
            Text(
                text = buildAnnotatedString {
                    append(total)
                    append(" ")
                    withStyle(SpanStyle(fontFamily = monoStyle.fontFamily, color = colors.ink)) {
                        append(totalSizeFormatted)
                    }
                },
                style = textStyle,
                color = colors.inkMuted,
                maxLines = 1,
            )
        }
        Spacer(Modifier.weight(1f))
        footer.path?.let { path ->
            FooterPath(
                path = path,
                onClick = { onCopyPath(path) },
            )
        }
    }
}

/** Full device path, copied to the clipboard on click. */
@Composable
private fun FooterPath(
    path: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val color by animateColorAsState(if (hovered) colors.ink else colors.inkFaint)
    WithTooltip(stringResource(Res.string.files_footer_copy_path), modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(PathRadius))
                .hoverable(interactionSource)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Text(
                text = path,
                style = FloconTheme.typography.monoMedium,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            FloconIcon(
                imageVector = Icons.Outlined.ContentCopy,
                tint = color,
                modifier = Modifier.size(PathIconSize),
            )
        }
    }
}

@Preview
@Composable
private fun FilesFooterPreview() {
    FloconTheme {
        FilesFooter(
            footer = previewFilesStateUiModel().footer,
            totalSizeFormatted = "48.6 KB",
            onCopyPath = {},
            modifier = Modifier.background(FloconTheme.colors.bg100),
        )
    }
}

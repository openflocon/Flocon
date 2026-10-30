package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.files_footer_counts
import flocondesktop.composeapp.generated.resources.files_footer_folders
import flocondesktop.composeapp.generated.resources.files_footer_items
import flocondesktop.composeapp.generated.resources.files_footer_total
import io.github.openflocon.flocondesktop.features.files.model.FilesFooterUiModel
import io.github.openflocon.flocondesktop.features.files.model.previewFilesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val DotSize = 3.dp

/** "N items · M folders • Total 48.6 KB ... /data/user/0/app/files" */
@Composable
internal fun FilesFooter(
    footer: FilesFooterUiModel,
    totalSizeFormatted: String?,
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
        footer.path?.let {
            Text(
                text = it,
                style = FloconTheme.typography.monoMedium,
                color = colors.inkFaint,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
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
            modifier = Modifier.background(FloconTheme.colors.bg100),
        )
    }
}

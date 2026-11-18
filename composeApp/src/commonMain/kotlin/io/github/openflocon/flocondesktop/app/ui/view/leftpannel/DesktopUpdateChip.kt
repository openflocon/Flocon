package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.desktop_update_chip
import flocondesktop.composeapp.generated.resources.desktop_update_installed
import flocondesktop.composeapp.generated.resources.desktop_update_title
import flocondesktop.composeapp.generated.resources.download
import flocondesktop.composeapp.generated.resources.sdk_update_release_notes
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel.DesktopUpdateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource

private val PopoverWidth = 240.dp

/** Quiet chip in the footer of the left panel: a newer desktop app exists. The click opens the details. */
@Composable
internal fun DesktopUpdateChip(
    update: DesktopUpdateUiModel,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var showDetails by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val colors = FloconTheme.colors
    val label = stringResource(Res.string.desktop_update_chip, update.latestVersion)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .then(if (expanded) Modifier.fillMaxWidth() else Modifier)
                .clip(FloconTheme.shapes.medium)
                .background(colors.accentSoft)
                .clickable { showDetails = true }
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = label,
                tint = colors.accent,
                modifier = Modifier.size(16.dp),
            )
            if (expanded) {
                Text(
                    text = label,
                    color = colors.accent,
                    style = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }

        FloconDropdownMenu(
            expanded = showDetails,
            onDismissRequest = { showDetails = false },
        ) {
            Column(
                modifier = Modifier
                    .width(PopoverWidth)
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(Res.string.desktop_update_title, update.latestVersion),
                    color = colors.ink,
                    style = FloconTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(Res.string.desktop_update_installed, update.currentVersion),
                    color = colors.inkFaint,
                    style = FloconTheme.typography.bodySmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
                    FloconButton(onClick = { uriHandler.openUri(update.link) }) {
                        Text(stringResource(Res.string.download))
                    }
                    FloconOutlinedButton(onClick = { uriHandler.openUri(update.link) }) {
                        Text(stringResource(Res.string.sdk_update_release_notes))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun DesktopUpdateChipPreview() {
    FloconTheme {
        DesktopUpdateChip(
            update = DesktopUpdateUiModel(
                currentVersion = "1.3.2",
                latestVersion = "1.4.0",
                link = "https://github.com/openflocon/Flocon/releases/tag/1.4.0",
            ),
            expanded = true,
        )
    }
}

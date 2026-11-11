package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.sdk_update_badge
import flocondesktop.composeapp.generated.resources.sdk_update_copy
import flocondesktop.composeapp.generated.resources.sdk_update_ignore
import flocondesktop.composeapp.generated.resources.sdk_update_release_notes
import flocondesktop.composeapp.generated.resources.sdk_update_running
import flocondesktop.composeapp.generated.resources.sdk_update_title
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel.SdkUpdateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource

private val PopoverWidth = 300.dp

/** Badge next to the device / app selector: a newer SDK exists for the selected app. The click opens the details. */
@Composable
internal fun SdkUpdateBadge(
    update: SdkUpdateUiModel,
    onIgnore: (SdkUpdateUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val colors = FloconTheme.colors

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.statusWarning.copy(alpha = 0.16f))
                .clickable { expanded = true }
                .pointerHoverIcon(PointerIcon.Hand)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = null,
                tint = colors.statusWarning,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = stringResource(Res.string.sdk_update_badge, update.currentVersion, update.latestVersion),
                color = colors.statusWarning,
                style = FloconTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column(
                modifier = Modifier
                    .width(PopoverWidth)
                    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(Res.string.sdk_update_title),
                    color = colors.ink,
                    style = FloconTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(Res.string.sdk_update_running, update.currentVersion, update.latestVersion),
                    color = colors.inkFaint,
                    style = FloconTheme.typography.bodySmall,
                )
                Text(
                    text = update.dependency,
                    color = colors.ink,
                    style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .background(colors.bg100, FloconTheme.shapes.small)
                        .padding(FloconTheme.spacing.small),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
                    FloconButton(onClick = { clipboardManager.setText(AnnotatedString(update.dependency)) }) {
                        Text(stringResource(Res.string.sdk_update_copy))
                    }
                    FloconOutlinedButton(onClick = { uriHandler.openUri(update.releaseNotesLink) }) {
                        Text(stringResource(Res.string.sdk_update_release_notes))
                    }
                    FloconOutlinedButton(
                        onClick = {
                            expanded = false
                            onIgnore(update)
                        },
                    ) {
                        Text(stringResource(Res.string.sdk_update_ignore))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun SdkUpdateBadgePreview() {
    FloconTheme {
        SdkUpdateBadge(
            update = SdkUpdateUiModel(
                currentVersion = "1.2.0",
                latestVersion = "1.4.0",
                dependency = "io.github.openflocon:flocon:1.4.0",
                releaseNotesLink = "https://github.com/openflocon/Flocon/releases/tag/1.4.0",
            ),
            onIgnore = {},
        )
    }
}

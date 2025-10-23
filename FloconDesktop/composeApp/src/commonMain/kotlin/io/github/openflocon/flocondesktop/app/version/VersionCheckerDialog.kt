package io.github.openflocon.flocondesktop.app.version


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BoxScope.VersionCheckerView() {
    val viewmodel = koinViewModel<VersionCheckerViewModel>()
    val version by viewmodel.versionAvailable.collectAsStateWithLifecycle()
    version?.let {
        VersionCheckerDialog(
            modifier = Modifier.align(Alignment.BottomEnd),
            version = it,
            onDismiss = viewmodel::hideNewVersionDialog,
        )
    }
}


@Composable
private fun VersionCheckerDialog(
    modifier: Modifier,
    version: VersionCheckerViewModel.VersionAvailableUiModel,
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = modifier
            .padding(16.dp)
            .clickable(
                onClick = {
                    // intercept click behind
                }
            )
            .background(
                color = FloconTheme.colorPalette.tertiary,
                shape = FloconTheme.shapes.medium
            )
            .padding(horizontal = 12.dp, 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "New version available: ${version.version}",
                color = FloconTheme.colorPalette.onTertiary,
                style = FloconTheme.typography.titleSmall,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloconButton(
                    containerColor = FloconTheme.colorPalette.primary,
                    onClick = {
                        uriHandler.openUri(version.link)
                        onDismiss()
                    }) {
                    Text("Download", color = FloconTheme.colorPalette.onPrimary)
                }
                FloconOutlinedButton(
                    onClick = onDismiss,
                    borderColor = FloconTheme.colorPalette.primary
                ) {
                    Text("Close", color = FloconTheme.colorPalette.primary)
                }
            }
        }
    }
}

@Composable
@Preview
private fun VersionCheckerDialogPreview() {
    FloconTheme {
        VersionCheckerDialog(
            modifier = Modifier,
            onDismiss = {},
            version = VersionCheckerViewModel.VersionAvailableUiModel(
                version = "1.0.0",
                link = "https://github.com/openflocon/flocon-desktop",
            )
        )
    }
}

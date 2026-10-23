package io.github.openflocon.flocondesktop.app.version

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.close
import flocondesktop.composeapp.generated.resources.download
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BoxScope.VersionChecker() {
    val viewmodel = koinViewModel<VersionCheckerViewModel>()
    val state by viewmodel.state.collectAsStateWithLifecycle()
    state?.let {
        VersionCheckerDialog(
            modifier = Modifier.align(Alignment.BottomEnd),
            state = it,
            onDesktopDismiss = viewmodel::hideDesktopNewVersionDialog,
            onClientDismiss = viewmodel::hideClientNewVersionDialog,
        )
    }
}

@Composable
private fun VersionCheckerDialog(
    state: VersionCheckerViewModel.VersionAvailableState,
    onClientDismiss: (VersionCheckerViewModel.VersionAvailableUiModel) -> Unit,
    onDesktopDismiss: (VersionCheckerViewModel.VersionAvailableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(FloconTheme.spacing.large),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
    ) {
        state.client?.let {
            VersionCheckerDialog(
                modifier = Modifier, version = it, onDismiss = onClientDismiss
            )
        }
        state.desktop?.let {
            VersionCheckerDialog(
                modifier = Modifier, version = it, onDismiss = onDesktopDismiss
            )
        }
    }
}

@Composable
private fun VersionCheckerDialog(
    version: VersionCheckerViewModel.VersionAvailableUiModel,
    onDismiss: (VersionCheckerViewModel.VersionAvailableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = modifier
            .width(300.dp)
            .clickable(
                onClick = {
                    // intercept click behind
                }
            ).background(
                color = FloconTheme.colors.ink, shape = FloconTheme.shapes.medium
            ).padding(horizontal = FloconTheme.spacing.medium, 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = version.title,
                color = FloconTheme.colors.bg100,
                style = FloconTheme.typography.titleSmall,
            )

            version.subtitle?.let {
                Text(
                    text = it,
                    color = FloconTheme.colors.bg100,
                    style = FloconTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(FloconTheme.spacing.small))

            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloconButton(
                    containerColor = FloconTheme.colors.bg100, onClick = {
                        uriHandler.openUri(version.link)
                        onDismiss(version)
                    }
                ) {
                    Text(stringResource(Res.string.download), color = FloconTheme.colors.ink)
                }
                FloconOutlinedButton(
                    onClick = {
                        onDismiss(version)
                    }, borderColor = FloconTheme.colors.bg100
                ) {
                    Text(stringResource(Res.string.close), color = FloconTheme.colors.bg100)
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
                title = "New destkop version available: 1.0.0",
                subtitle = "subtitle",
            )
        )
    }
}

package io.github.openflocon.flocondesktop.app.ui.view.topbar.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Router
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.topbar_connect
import flocondesktop.composeapp.generated.resources.topbar_proxy
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/** Standalone button, on purpose not part of the [TopBarActions] group. */
@Composable
internal fun TopBarConnectButton(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBarStandaloneButton(
        imageVector = Icons.Outlined.Laptop,
        contentDescription = stringResource(Res.string.topbar_connect),
        onClicked = onClicked,
        modifier = modifier,
    )
}

/** Opens the proxy mode: see the calls of a phone without the SDK. */
@Composable
internal fun TopBarProxyButton(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBarStandaloneButton(
        imageVector = Icons.Outlined.Router,
        contentDescription = stringResource(Res.string.topbar_proxy),
        onClicked = onClicked,
        modifier = modifier,
    )
}

@Composable
private fun TopBarStandaloneButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Box(
        modifier = modifier
            .background(colors.bg100, FloconTheme.shapes.medium)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.extraSmall),
    ) {
        TopBarButton(
            active = false,
            imageVector = imageVector,
            contentDescription = contentDescription,
            onClicked = onClicked,
            isEnabled = true,
        )
    }
}

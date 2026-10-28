package io.github.openflocon.flocondesktop.app.ui.view.topbar.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.topbar_connect
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/** Standalone button, on purpose not part of the [TopBarActions] group. */
@Composable
internal fun TopBarConnectButton(
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
            imageVector = Icons.Outlined.Laptop,
            contentDescription = stringResource(Res.string.topbar_connect),
            onClicked = onClicked,
            isEnabled = true,
        )
    }
}

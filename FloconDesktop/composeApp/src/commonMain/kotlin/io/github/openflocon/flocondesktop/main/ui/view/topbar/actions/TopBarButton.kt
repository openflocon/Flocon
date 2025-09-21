package io.github.openflocon.flocondesktop.main.ui.view.topbar.actions

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton

@Composable
internal fun TopBarButton(
    active: Boolean,
    imageVector: ImageVector,
    contentDescription: String,
    isEnabled: Boolean,
    onClicked: () -> Unit,
) {
    TopBarButton(
        active = active,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        onClicked = onClicked,
    ) {
        FloconIcon(
            imageVector = imageVector,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
internal fun TopBarButton(
    active: Boolean,
    contentDescription: String,
    isEnabled: Boolean,
    onClicked: () -> Unit,
    content: @Composable () -> Unit,
) {
    FloconIconToggleButton(
        value = active,
        onValueChange = { onClicked() },
        size = 48.dp,
        enabled = isEnabled,
        tooltip = contentDescription
    ) {
        content()
    }
}

package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedTrackColor = FloconTheme.colorPalette.onSecondary,
            uncheckedTrackColor = FloconTheme.colorPalette.onTertiary
        ),
        modifier = modifier.scale(0.5f)
    )
}

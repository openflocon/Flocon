package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    CompositionLocalProvider(LocalDensity provides Density(0.8f)) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = FloconTheme.colorPalette.accent,
                checkedThumbColor = FloconTheme.colorPalette.onAccent,
                uncheckedThumbColor = FloconTheme.colorPalette.onSecondary,
                uncheckedTrackColor = FloconTheme.colorPalette.secondary
            ),
            modifier = modifier
        )
    }
}

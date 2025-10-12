package io.github.openflocon.library.designsystem.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    uncheckedColor: Color = FloconTheme.colorPalette.primary
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = FloconTheme.colorPalette.accent,
            uncheckedColor = uncheckedColor,
            checkmarkColor = FloconTheme.colorPalette.onAccent
        ),
        modifier = modifier
    )
}

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
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    uncheckedColor: Color = FloconTheme.colors.lineStrong
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = FloconTheme.colors.accent,
            uncheckedColor = uncheckedColor,
            checkmarkColor = FloconTheme.colors.accentInk
        ),
        modifier = modifier
    )
}

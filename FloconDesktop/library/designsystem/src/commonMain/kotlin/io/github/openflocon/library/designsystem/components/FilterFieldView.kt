package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DropdownFilterFieldView(
    value: String,
    onValueChanged: (value: String) -> Unit,
    placeholder: String = "Filter",
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FloconTextFieldWithoutM3(
            value = value,
            onValueChange = onValueChanged,
            placeholder = defaultPlaceHolder(
                placeholder,
                color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.5f)
            ),
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colorPalette.onSecondary,
            ),
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier.weight(1f).padding(all = 2.dp)
        )
    }
}

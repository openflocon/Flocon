package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun DropdownFilterField(
    value: String,
    onValueChanged: (value: String) -> Unit,
    placeholder: String = "Filter",
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        FloconTextFieldWithoutM3(
            value = value,
            onValueChange = onValueChanged,
            placeholder = defaultPlaceHolder(
                placeholder,
                color = FloconTheme.colors.inkFaint
            ),
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colors.inkMuted,
            ),
            containerColor = FloconTheme.colors.bg200,
            modifier = Modifier.weight(1f).padding(all = FloconTheme.spacing.tiny)
        )
    }
}

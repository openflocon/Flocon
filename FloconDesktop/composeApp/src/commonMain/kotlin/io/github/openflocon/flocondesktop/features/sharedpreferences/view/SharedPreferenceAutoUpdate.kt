package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_update
import flocondesktop.composeapp.generated.resources.refresh
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource

@Immutable
sealed interface PreferenceAutoUpdate {
    @Immutable
    data class Enabled(val delayMs: Int) : PreferenceAutoUpdate

    @Immutable
    object Disabled : PreferenceAutoUpdate
}

@Composable
fun PreferenceAutoUpdate(
    state: PreferenceAutoUpdate,
    onChange: (Boolean) -> Unit,
    onAutoUpdateDelayChanged: (delay: Int) -> Unit,
    refreshClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            is PreferenceAutoUpdate.Disabled -> {}
            is PreferenceAutoUpdate.Enabled -> {
                AutoUpdateTextField(
                    modifier = Modifier.width(100.dp),
                    placeholderText = "delayMs",
                    value = state.delayMs.toString(),
                    onValueChange = {
                        try {
                            onAutoUpdateDelayChanged(it.toInt())
                        } catch (t: Throwable) {
                            // no op
                        }
                    },
                )
            }
        }
        Checkbox(
            checked = state is PreferenceAutoUpdate.Enabled,
            onCheckedChange = {
                onChange(it)
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = FloconTheme.colorPalette.secondary,
                checkedColor = FloconTheme.colorPalette.secondary,
            )
        )
        Text(
            stringResource(Res.string.auto_update),
            color = FloconTheme.colorPalette.onPrimary,
            style = FloconTheme.typography.bodySmall,
            modifier = Modifier.clickable {
                val enabled = state is PreferenceAutoUpdate.Enabled
                onChange(!enabled)
            }
        )

        FloconIconButton(
            onClick = refreshClicked,
            tooltip = stringResource(Res.string.refresh),
        ) {
            FloconIcon(
                imageVector = Icons.Outlined.Refresh,
            )
        }
    }
}

@Composable
private fun AutoUpdateTextField(
    placeholderText: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onValueChangedCallback by rememberUpdatedState(onValueChange)

    FloconTextFieldWithoutM3(
        value = value,
        onValueChange = {
            onValueChangedCallback(it)
        },
        placeholder = defaultPlaceHolder(placeholderText),
        leadingComponent = {},
        containerColor = FloconTheme.colorPalette.secondary,
        trailingComponent = {
            Text(text = "ms", style = FloconTheme.typography.bodySmall)
        },
        textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
        modifier = modifier
    )
}

package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator

@Composable
internal fun SharedPrefSelectorView(
    sharedPrefsState: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (sharedPrefsState is SharedPrefsStateUiModel.WithContent)
                expanded = true
        },
        anchorContent = {
            FloconButton(
                onClick = {
                    if (sharedPrefsState is SharedPrefsStateUiModel.WithContent)
                        expanded = true
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                when (sharedPrefsState) {
                    SharedPrefsStateUiModel.Empty -> Text("No SharedPreference")
                    SharedPrefsStateUiModel.Loading -> FloconLinearProgressIndicator()
                    is SharedPrefsStateUiModel.WithContent -> {
                        Text(text = sharedPrefsState.selected.name)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (sharedPrefsState is SharedPrefsStateUiModel.WithContent) {
            sharedPrefsState.sharedPrefs
                .forEach { pref ->
                    FloconDropdownMenuItem(
                        text = pref.name,
                        onClick = {
                            onSharedPrefSelected(pref)
                            expanded = false
                        }
                    )
                }
        }
    }
}

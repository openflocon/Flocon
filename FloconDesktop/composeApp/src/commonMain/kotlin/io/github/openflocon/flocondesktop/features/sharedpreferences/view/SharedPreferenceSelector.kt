package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preferences_empty
import flocondesktop.composeapp.generated.resources.preferences_selector_caption
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterField
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconPageSelector
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SharedPreferenceSelector(
    state: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (state is SharedPrefsStateUiModel.WithContent) {
                expanded = true
            }
        },
        anchorContent = {
            val onClick = {
                if (state is SharedPrefsStateUiModel.WithContent) {
                    expanded = true
                }
            }
            when (state) {
                SharedPrefsStateUiModel.Empty -> Unit

                SharedPrefsStateUiModel.Loading ->
                    FloconButton(onClick = onClick, containerColor = FloconTheme.colors.bg200) {
                        FloconLinearProgressIndicator()
                    }

                is SharedPrefsStateUiModel.WithContent -> FloconPageSelector(
                    icon = Icons.Outlined.Description,
                    caption = stringResource(Res.string.preferences_selector_caption),
                    value = state.selected.name,
                    onClick = onClick,
                )
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterField(
            value = filterText,
            onValueChanged = { filterText = it },
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (state is SharedPrefsStateUiModel.WithContent) {
                val filteredItems = remember(filterText, state.preferences) {
                    state.preferences.filter {
                        it.name.contains(filterText, ignoreCase = true)
                    }
                }

                filteredItems.fastForEach { preference ->
                    FloconDropdownMenuItem(
                        text = preference.name,
                        onClick = {
                            onSharedPrefSelected(preference)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

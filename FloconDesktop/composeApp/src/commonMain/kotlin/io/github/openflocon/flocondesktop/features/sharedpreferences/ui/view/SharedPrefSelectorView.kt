package io.github.openflocon.flocondesktop.features.sharedpreferences.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.SharedPrefsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.previewDeviceSharedPrefUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SharedPrefSelectorView(
    sharedPrefsState: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val contentPadding =
        PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp,
        )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "SharedPref : ",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.width(4.dp))

        when (sharedPrefsState) {
            SharedPrefsStateUiModel.Loading -> {
                // hide
            }

            SharedPrefsStateUiModel.Empty -> {
                Text(
                    modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.onBackground, shape = shape)
                        .padding(contentPadding),
                    text = "No SharedPreferences Found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background,
                )
            }

            is SharedPrefsStateUiModel.WithContent -> {
                var expanded by remember { mutableStateOf(false) }

                SharedPrefView(
                    sharedPref = sharedPrefsState.selected,
                    textColor = MaterialTheme.colorScheme.background,
                    modifier =
                    Modifier
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable { expanded = true }
                        .padding(contentPadding),
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    sharedPrefsState.sharedPrefs.forEach { SharedPref ->
                        DropdownMenuItem(
                            text = {
                                SharedPrefView(
                                    sharedPref = SharedPref,
                                    textColor = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(all = 4.dp),
                                )
                            },
                            onClick = {
                                onSharedPrefSelected(SharedPref)
                                expanded = false // Close the dropdown after selection
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedPrefView(
    sharedPref: DeviceSharedPrefUiModel,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = sharedPref.name, // Device Name
        color = textColor,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
    )
}

@Preview
@Composable
private fun SharedPrefViewPreview() {
    FloconTheme {
        SharedPrefView(
            sharedPref = previewDeviceSharedPrefUiModel(),
            textColor = MaterialTheme.colorScheme.background,
        )
    }
}

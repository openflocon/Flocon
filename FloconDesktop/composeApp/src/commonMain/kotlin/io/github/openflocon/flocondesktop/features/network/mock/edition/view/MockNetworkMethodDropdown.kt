package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkMethodUi

@Composable
fun MockNetworkMethodDropdown(
    label: String,
    value: MockNetworkMethodUi,
    onValueChange: (MockNetworkMethodUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        MockNetworkLabelView(label)

        Box {
            MockNetworkMethodView(
                method = value,
                onClick = {
                    expanded = true
                },
            )
            FloconDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                MockNetworkMethodUi.entries.forEach { method ->
                    MockNetworkMethodView(
                        modifier = Modifier.padding(all = 4.dp),
                        method = method,
                        onClick = {
                            onValueChange(method)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

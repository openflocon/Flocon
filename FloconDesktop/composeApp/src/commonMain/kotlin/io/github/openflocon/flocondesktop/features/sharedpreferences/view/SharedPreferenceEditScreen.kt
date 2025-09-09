package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField

@Composable
fun SharedPreferenceEditScreen(
    row: SharedPreferencesRowUiModel,
    stringValue: SharedPreferencesRowUiModel.Value.StringValue,
    cancel: () -> Unit,
    save: (SharedPreferencesRowUiModel, stringValue: String) -> Unit,
) {
    val windowState = remember(row) { createFloconWindowState() }
    FloconWindow(
        title = "SharedPreference",
        state = windowState,
        onCloseRequest = cancel,
    ) {
        SharedPreferenceEditScreenContent(
            row = row,
            stringValue = stringValue,
            save = save,
            cancel = cancel,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SharedPreferenceEditScreenContent(
    row: SharedPreferencesRowUiModel,
    stringValue: SharedPreferencesRowUiModel.Value.StringValue,
    modifier: Modifier = Modifier,
    cancel: () -> Unit,
    save: (SharedPreferencesRowUiModel, stringValue: String) -> Unit,
) {
    var value by remember { mutableStateOf(stringValue.value) }

    FloconSurface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            FloconTextField(
                value = value,
                modifier = Modifier.fillMaxWidth().weight(1f),
                onValueChange = { value = it },
            )
            FloconDialogButtons(
                onCancel = cancel,
                onValidate = {
                    save(row, value)
                },
            )
        }
    }
}

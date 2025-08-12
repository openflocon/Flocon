package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.ui.mapper.createEditable
import io.github.openflocon.flocondesktop.features.network.ui.mapper.editableToUi
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.SelectedMockUiModel
import java.util.UUID

@Composable
fun NetworkEditionWindow(
    state: SelectedMockUiModel,
    onCloseRequest: () -> Unit,
    onCancel: () -> Unit,
    onSave: (MockNetworkUiModel) -> Unit,
) {
    val windowState: FloconWindowState = remember {
        createFloconWindowState()
    }
    FloconWindow(
        title = "Mock Edition",
        state = windowState,
        onCloseRequest = onCloseRequest,
    ) {
        NetworkEditionContent(
            state = state,
            onCancel = onCancel,
            onSave = onSave,
        )
    }
}

@Composable
fun NetworkEditionContent(
    onCancel: () -> Unit,
    onSave: (MockNetworkUiModel) -> Unit,
    state: SelectedMockUiModel
) {
    MockEditorScreen(
        initialMock = state,
        onSave = onSave,
        onCancel = onCancel,
    )
}


@Composable
fun MockEditorScreen(
    initialMock: SelectedMockUiModel,
    onSave: (MockNetworkUiModel) -> Unit,
    onCancel: () -> Unit
) {
    var mock by remember { mutableStateOf(createEditable(initialMock)) }
    val scrollState = rememberScrollState()

    // TODO more granularity on error
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(
            onClick = {
                editableToUi(mock).fold(
                    doOnFailure = {
                        error = "All fields are required"
                    },
                    doOnSuccess = {
                        onSave(it)
                        error = null
                    }
                )

            }
        ) {
            Text("Save")
        }

        TextButton(
            onClick = {
                onCancel()
            }
        ) {
            Text("Cancel")
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Expectation
            Text(
                text = "Expectation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = mock.expectation.urlPattern ?: "",
                onValueChange = { newValue ->
                    mock = mock.copy(expectation = mock.expectation.copy(urlPattern = newValue))
                },
                label = { Text("URL Pattern") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mock.expectation.method ?: "",
                onValueChange = { newValue ->
                    mock = mock.copy(expectation = mock.expectation.copy(method = newValue))
                },
                label = { Text("Method") },
                modifier = Modifier.fillMaxWidth()
            )

            // Section Response
            Text(
                text = "Response",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = mock.response.httpCode.toString(),
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        mock = mock.copy(
                            response = mock.response.copy(
                                httpCode = newValue.toIntOrNull() ?: 0
                            )
                        )
                    }
                },
                label = { Text("HTTP Code") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mock.response.body ?: "",
                onValueChange = { newValue ->
                    mock = mock.copy(response = mock.response.copy(body = newValue))
                },
                label = { Text("Body") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mock.response.mediaType,
                onValueChange = { newValue ->
                    mock = mock.copy(response = mock.response.copy(mediaType = newValue))
                },
                label = { Text("Media Type") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mock.response.delay.toString(),
                onValueChange = { newValue ->
                    // On vérifie si la nouvelle valeur est vide ou si elle contient uniquement des chiffres
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        // Si c'est le cas, on met à jour l'état
                        val newDelay = newValue.toLongOrNull() ?: 0L
                        mock = mock.copy(response = mock.response.copy(delay = newDelay))
                    }
                },
                label = { Text("Delay (ms)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Section Headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Headers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        val newHeaders = mock.response.headers.toMutableMap().apply {
                            this[UUID.randomUUID().toString()] = ""
                        }
                        mock = mock.copy(response = mock.response.copy(headers = newHeaders))
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Header")
                }
            }

            mock.response.headers.forEach { (key, value) ->
                HeaderInputField(
                    key = key,
                    value = value,
                    onKeyChange = { newKey ->
                        val newHeaders = mock.response.headers.toMutableMap().apply {
                            val originalKey =
                                this.entries.find { it.value == value && it.key == key }?.key
                            if (originalKey != null) {
                                this.remove(originalKey)
                                this[newKey] = value
                            }
                        }
                        mock = mock.copy(response = mock.response.copy(headers = newHeaders))
                    },
                    onValueChange = { newValue ->
                        val newHeaders = mock.response.headers.toMutableMap().apply {
                            this[key] = newValue
                        }
                        mock = mock.copy(response = mock.response.copy(headers = newHeaders))
                    },
                    onRemove = {
                        val newHeaders = mock.response.headers.toMutableMap().apply {
                            remove(key)
                        }
                        mock = mock.copy(response = mock.response.copy(headers = newHeaders))
                    }
                )
            }
        }
    }
}


@Composable
private fun HeaderInputField(
    key: String,
    value: String,
    onKeyChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = key,
            onValueChange = onKeyChange,
            label = { Text("Key") },
            modifier = Modifier.weight(0.45f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Value") },
            modifier = Modifier.weight(0.45f)
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Header")
        }
    }
}

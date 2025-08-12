package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.ui.mapper.createEditable
import io.github.openflocon.flocondesktop.features.network.ui.mapper.editableToUi
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.SelectedMockUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
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
        FloconSurface {
            NetworkEditionContent(
                state = state,
                onCancel = onCancel,
                onSave = onSave,
            )
        }
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.panel)
                .padding(all = 12.dp)
        ) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = {
                    onCancel()
                }
            ) {
                Text("Cancel")
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
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
            NetworkMockFieldView(
                value = mock.expectation.urlPattern ?: "",
                onValueChange = { newValue ->
                    mock = mock.copy(expectation = mock.expectation.copy(urlPattern = newValue))
                },
                label = "URL Pattern",
                placeHolder = "https://www.myDomain.*",
                modifier = Modifier.fillMaxWidth()
            )
            NetworkMockFieldView(
                // TODO should be a dropdown
                label = "Method",
                value = mock.expectation.method ?: "",
                onValueChange = { newValue ->
                    mock = mock.copy(expectation = mock.expectation.copy(method = newValue))
                },
                placeHolder = "GET",
                modifier = Modifier.fillMaxWidth()
            )

            // Section Response
            Text(
                text = "Response",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            NetworkMockFieldView(
                label = "HTTP Code",
                placeHolder = "eg: 200",
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
                modifier = Modifier.fillMaxWidth()
            )
            NetworkMockFieldView(
                label = "Body",
                value = mock.response.body ?: "",
                placeHolder = null,
                onValueChange = { newValue ->
                    mock = mock.copy(response = mock.response.copy(body = newValue))
                },
                modifier = Modifier.fillMaxWidth()
            )
            NetworkMockFieldView(
                label = "Media Type",
                value = mock.response.mediaType,
                placeHolder = "application/json",
                onValueChange = { newValue ->
                    mock = mock.copy(response = mock.response.copy(mediaType = newValue))
                },
                modifier = Modifier.fillMaxWidth()
            )
            NetworkMockFieldView(
                label = "Delay (ms)",
                value = mock.response.delay.toString(),
                placeHolder = "0",
                onValueChange = { newValue ->
                    // On vérifie si la nouvelle valeur est vide ou si elle contient uniquement des chiffres
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        // Si c'est le cas, on met à jour l'état
                        val newDelay = newValue.toLongOrNull() ?: 0L
                        mock = mock.copy(response = mock.response.copy(delay = newDelay))
                    }
                },
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

@Composable
fun NetworkMockFieldView(
    label :String,
    placeHolder: String?,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            label,
            modifier = Modifier.padding(start = 4.dp),
            color = FloconTheme.colorPalette.onSurface,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Thin,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                placeHolder?.takeIf { value.isEmpty() }?.let {
                    Text(
                        text = it,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                    )
                }
                BasicTextField(
                    textStyle = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                    onValueChange = {
                        onValueChange(it)
                    },
                )
            }
        }
    }
}

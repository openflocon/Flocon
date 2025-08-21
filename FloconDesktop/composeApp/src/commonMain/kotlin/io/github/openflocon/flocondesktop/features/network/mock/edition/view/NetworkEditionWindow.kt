package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.createEditable
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.editableToUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.HeaderUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

@Composable
fun NetworkEditionWindow(
    instanceId: String,
    state: SelectedMockUiModel,
    onCloseRequest: () -> Unit,
    onCancel: () -> Unit,
    onSave: (MockNetworkUiModel) -> Unit,
) {
    val windowState: FloconWindowState = remember(instanceId) {
        createFloconWindowState()
    }
    key(windowState, instanceId) {
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
}

@Composable
fun NetworkEditionContent(
    onCancel: () -> Unit,
    onSave: (MockNetworkUiModel) -> Unit,
    state: SelectedMockUiModel,
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
    onCancel: () -> Unit,
) {
    var mock by remember { mutableStateOf(createEditable(initialMock)) }
    val scrollState = rememberScrollState()

    // TODO more granularity on error
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.panel)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable(onClick = onCancel)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Cancel",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.panel,
                )
            }
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable(onClick = {
                        editableToUi(mock).fold(
                            doOnFailure = {
                                error = "Some fields are required"
                            },
                            doOnSuccess = {
                                onSave(it)
                                error = null
                            },
                        )
                    })
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Save",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.panel,
                )
            }
        }

        error?.let {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = it, color = MaterialTheme.colorScheme.error,
            )
        }

        Row(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Section Expectation
                Text(
                    text = "Expectation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                FloconTextField(
                    value = mock.expectation.urlPattern ?: "",
                    onValueChange = { newValue ->
                        mock = mock.copy(expectation = mock.expectation.copy(urlPattern = newValue))
                    },
                    label = defaultLabel("URL Pattern"),
                    placeholder = defaultPlaceHolder("https://www.myDomain.*"),
                    modifier = Modifier.fillMaxWidth(),
                )
                MockNetworkMethodDropdown(
                    // TODO should be a dropdown
                    label = "Method",
                    value = mock.expectation.method,
                    onValueChange = { newValue ->
                        mock = mock.copy(expectation = mock.expectation.copy(method = newValue))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Section Response
                Text(
                    text = "Response",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FloconTextField(
                        label = defaultLabel("HTTP Code"),
                        maxLines = 1,
                        placeholder = defaultPlaceHolder("eg: 200"),
                        value = mock.response.httpCode.toString(),
                        containerColor = FloconTheme.colorPalette.panel,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                mock = mock.copy(
                                    response = mock.response.copy(
                                        httpCode = newValue.toIntOrNull() ?: 0,
                                    ),
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    FloconTextField(
                        label = defaultLabel("Additional Delay (ms)"),
                        maxLines = 1,
                        value = mock.response.delay.toString(),
                        placeholder = defaultPlaceHolder("0"),
                        containerColor = FloconTheme.colorPalette.panel,
                        onValueChange = { newValue ->
                            // On vérifie si la nouvelle valeur est vide ou si elle contient uniquement des chiffres
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                // Si c'est le cas, on met à jour l'état
                                val newDelay = newValue.toLongOrNull() ?: 0L
                                mock = mock.copy(response = mock.response.copy(delay = newDelay))
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                FloconTextField(
                    label = defaultLabel("Media Type"),
                    maxLines = 1,
                    value = mock.response.mediaType,
                    placeholder = defaultPlaceHolder("application/json"),
                    containerColor = FloconTheme.colorPalette.panel,
                    onValueChange = { newValue ->
                        mock = mock.copy(response = mock.response.copy(mediaType = newValue))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NetworkMockMediaType(
                        text = "application/json",
                        onClicked = {
                            mock = mock.copy(response = mock.response.copy(mediaType = it))
                        },
                    )
                    NetworkMockMediaType(
                        text = "text/plain",
                        onClicked = {
                            mock = mock.copy(response = mock.response.copy(mediaType = it))
                        },
                    )
                }

                // Section Headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MockNetworkLabelView("Headers")
                    Box(
                        modifier = Modifier.size(28.dp)
                            .clip(
                                RoundedCornerShape(4.dp),
                            ).clickable {
                                val newHeaders = mock.response.headers + HeaderUiModel(
                                    key = "",
                                    value = "",
                                )
                                mock =
                                    mock.copy(response = mock.response.copy(headers = newHeaders))
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Header",
                            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface),
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    mock.response.headers.fastForEach { header ->
                        HeaderInputField(
                            key = header.key,
                            value = header.value,
                            onKeyChange = { newKey ->
                                val newHeaders = mock.response.headers.map {
                                    if (it.id == header.id) {
                                        it.copy(key = newKey)
                                    } else {
                                        it
                                    }
                                }
                                mock =
                                    mock.copy(response = mock.response.copy(headers = newHeaders))
                            },
                            onValueChange = { newValue ->
                                val newHeaders = mock.response.headers.map {
                                    if (it.id == header.id) {
                                        it.copy(value = newValue)
                                    } else {
                                        it
                                    }
                                }
                                mock =
                                    mock.copy(response = mock.response.copy(headers = newHeaders))
                            },
                            onRemove = {
                                val newHeaders =
                                    mock.response.headers.filterNot { it.id == header.id }
                                mock =
                                    mock.copy(response = mock.response.copy(headers = newHeaders))
                            },
                        )
                    }
                }

                FloconTextField(
                    label = defaultLabel("Body"),
                    value = mock.response.body,
                    minLines = 4,
                    onValueChange = { newValue ->
                        mock = mock.copy(response = mock.response.copy(body = newValue))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = FloconTheme.colorPalette.panel
                )
            }
        }
    }
}

@Composable
fun NetworkMockMediaType(text: String, onClicked: (text: String) -> Unit) {
    Text(
        text = text,
        color = FloconTheme.colorPalette.onPanel,
        style = FloconTheme.typography.bodySmall,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FloconTheme.colorPalette.panel.copy(alpha = 0.8f))
            .clickable {
                onClicked(text)
            }
            .padding(horizontal = 12.dp, vertical = 2.dp),
    )
}

@Composable
private fun HeaderInputField(
    key: String,
    value: String,
    onKeyChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.weight(1f)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                ).padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (key.isEmpty()) {
                Text(
                    text = "Key",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                )
            }
            FloconTextField(
                value = value,
                onValueChange = onKeyChange,
                textStyle = FloconTheme.typography.bodySmall.copy(
                    color = FloconTheme.colorPalette.onSurface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Box(
            Modifier.weight(1f)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                ).padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "Value",
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                )
            }
            FloconTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(2.dp))
                .clickable {
                    onRemove()
                }.padding(all = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Header",
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface),
            )
        }
    }
}

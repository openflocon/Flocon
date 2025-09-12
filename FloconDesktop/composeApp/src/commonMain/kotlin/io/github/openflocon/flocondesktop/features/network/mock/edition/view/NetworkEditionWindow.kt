package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.flocondesktop.features.network.badquality.edition.view.NetworkExceptionSelector
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.createEditable
import io.github.openflocon.flocondesktop.features.network.mock.edition.mapper.editableToUi
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.EditableMockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.HeaderUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
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
        createFloconWindowState(
            size = DpSize(900.dp, 700.dp)
        )
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
        FloconDialogHeader(
            modifier = Modifier
                .fillMaxWidth(),
            title = "Mock Edition"
        )

        error?.let {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = it, color = MaterialTheme.colorScheme.error,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
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
                    containerColor = FloconTheme.colorPalette.primary
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

                FloconTextField(
                    label = defaultLabel("Additional Delay (ms)"),
                    maxLines = 1,
                    value = mock.delay.toString(),
                    placeholder = defaultPlaceHolder("0"),
                    containerColor = FloconTheme.colorPalette.primary,
                    onValueChange = { newValue ->
                        // On vérifie si la nouvelle valeur est vide ou si elle contient uniquement des chiffres
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            // Si c'est le cas, on met à jour l'état
                            val newDelay = newValue.toLongOrNull() ?: 0L
                            mock = mock.copy(delay = newDelay)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Type :",
                    style = MaterialTheme.typography.titleMedium,
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Tab(
                        text = "HttpCode + Body",
                        isSelected = mock.responseType == EditableMockNetworkUiModel.ResponseType.BODY,
                        onSelected = {
                            mock =
                                mock.copy(responseType = EditableMockNetworkUiModel.ResponseType.BODY)
                        }
                    )
                    Tab(
                        text = "Exception",
                        isSelected = mock.responseType == EditableMockNetworkUiModel.ResponseType.EXCEPTION,
                        onSelected = {
                            mock =
                                mock.copy(responseType = EditableMockNetworkUiModel.ResponseType.EXCEPTION)
                        }
                    )
                }
                when (mock.responseType) {
                    EditableMockNetworkUiModel.ResponseType.EXCEPTION -> {
                        NetworkExceptionSelector(
                            selected = mock.exceptionResponse.classPath,
                            onSelected = { new ->
                                mock = mock.copy(
                                    exceptionResponse = mock.exceptionResponse.copy(
                                        classPath = new,
                                    ),
                                )
                            }
                        )
                    }

                    EditableMockNetworkUiModel.ResponseType.BODY -> {
                        val bodyResponse = mock.bodyResponse

                        FloconTextField(
                            label = defaultLabel("HTTP Code"),
                            maxLines = 1,
                            placeholder = defaultPlaceHolder("eg: 200"),
                            value = bodyResponse.httpCode.toString(),
                            containerColor = FloconTheme.colorPalette.primary,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                    mock = mock.copy(
                                        bodyResponse = bodyResponse.copy(
                                            httpCode = newValue.toIntOrNull() ?: 0,
                                        ),
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        FloconTextField(
                            label = defaultLabel("Media Type"),
                            maxLines = 1,
                            value = bodyResponse.mediaType,
                            placeholder = defaultPlaceHolder("application/json"),
                            containerColor = FloconTheme.colorPalette.primary,
                            onValueChange = { newValue ->
                                mock = mock.copy(
                                    bodyResponse = bodyResponse.copy(mediaType = newValue)
                                )
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
                                    mock = mock.copy(
                                        bodyResponse = bodyResponse.copy(mediaType = it)
                                    )
                                },
                            )
                            NetworkMockMediaType(
                                text = "text/plain",
                                onClicked = {
                                    mock = mock.copy(
                                        bodyResponse = bodyResponse.copy(mediaType = it)
                                    )
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
                                        val newHeaders = bodyResponse.headers + HeaderUiModel(
                                            key = "",
                                            value = "",
                                        )
                                        mock =
                                            mock.copy(
                                                bodyResponse = bodyResponse.copy(headers = newHeaders)
                                            )
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
                            bodyResponse.headers.fastForEach { header ->
                                HeaderInputField(
                                    key = header.key,
                                    value = header.value,
                                    onKeyChange = { newKey ->
                                        val newHeaders = bodyResponse.headers.map {
                                            if (it.id == header.id) {
                                                it.copy(key = newKey)
                                            } else {
                                                it
                                            }
                                        }
                                        mock =
                                            mock.copy(
                                                bodyResponse = bodyResponse.copy(headers = newHeaders)
                                            )
                                    },
                                    onValueChange = { newValue ->
                                        val newHeaders = bodyResponse.headers.map {
                                            if (it.id == header.id) {
                                                it.copy(value = newValue)
                                            } else {
                                                it
                                            }
                                        }
                                        mock = mock.copy(
                                            bodyResponse = bodyResponse.copy(headers = newHeaders)
                                        )
                                    },
                                    onRemove = {
                                        val newHeaders =
                                            bodyResponse.headers.filterNot { it.id == header.id }
                                        mock = mock.copy(
                                            bodyResponse = bodyResponse.copy(headers = newHeaders)
                                        )
                                    },
                                )
                            }
                        }

                        FloconTextField(
                            label = defaultLabel("Body"),
                            value = bodyResponse.body,
                            minLines = 4,
                            onValueChange = { newValue ->
                                mock = mock.copy(bodyResponse = bodyResponse.copy(body = newValue))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = FloconTheme.colorPalette.primary
                        )
                    }
                }
            }
        }
        FloconDialogButtons(
            onCancel = onCancel,
            onValidate = {
                editableToUi(mock).fold(
                    doOnFailure = {
                        error = "Some fields are required"
                    },
                    doOnSuccess = {
                        onSave(it)
                        error = null
                    },
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
        )
    }
}

@Composable
fun NetworkMockMediaType(text: String, onClicked: (text: String) -> Unit) {
    Text(
        text = text,
        color = FloconTheme.colorPalette.onPrimary,
        style = FloconTheme.typography.bodySmall,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FloconTheme.colorPalette.primary.copy(alpha = 0.8f))
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
        FloconTextField(
            value = key,
            onValueChange = onKeyChange,
            placeholder = defaultPlaceHolder("Key"),
            modifier = Modifier.weight(1f),
            containerColor = FloconTheme.colorPalette.primary
        )
        FloconTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = defaultPlaceHolder("Value"),
            modifier = Modifier.weight(1f),
            containerColor = FloconTheme.colorPalette.primary
        )

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

@Composable
fun RowScope.Tab(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Text(
        modifier = Modifier.weight(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = if (isSelected) {
                    Color.White.copy(alpha = 0.8f)
                } else {
                    Color.White.copy(alpha = 0.1f)
                },
            ).clickable {
                onSelected()
            }.padding(vertical = 8.dp),
        text = text,
        textAlign = TextAlign.Center,
        style = FloconTheme.typography.bodyMedium,
        color = if(isSelected) {
            FloconTheme.colorPalette.primary
        } else {
            FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f)
        },
    )
}

@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.view.badquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.model.badquality.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.model.badquality.previewBadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.view.mocks.MockEditorScreen
import io.github.openflocon.flocondesktop.features.network.view.mocks.MockNetworkLabelView
import io.github.openflocon.flocondesktop.features.network.view.mocks.NetworkMockFieldView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BadNetworkQualityWindow(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BadQualityNetworkViewModel = koinViewModel()
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    val viewModelEvent by viewModel.events.collectAsStateWithLifecycle(null)
    LaunchedEffect(viewModelEvent) {
        when (viewModelEvent) {
            BadQualityNetworkViewModel.Event.Close -> onCloseRequest()
            null -> {}
        }
    }

    BasicAlertDialog(
        onDismissRequest = onCloseRequest,
    ) {
        BadNetworkQualityContent(
            state = state,
            save = viewModel::save,
            close = { onCloseRequest() },
        )
    }
}

@Composable
@Preview
private fun BadNetworkQualityContentPreview() {
    FloconTheme {
        FloconSurface {
            BadNetworkQualityContent(
                state = previewBadQualityConfigUiModel(
                    errorCount = 5
                ),
                save = {},
                close = {},
            )
        }
    }
}


@Composable
fun BadNetworkQualityContent(
    state: BadQualityConfigUiModel?,
    close: () -> Unit,
    save: (state: BadQualityConfigUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editableState: BadQualityConfigUiModel by remember(state) { mutableStateOf(toEditableState(state)) }
    Column(
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MockNetworkLabelView("isEnabled")
            Switch(
                modifier = Modifier.scale(0.6f),
                checked = editableState.isEnabled,
                onCheckedChange = {
                    editableState.copy(isEnabled = it)
                },
            )
        }
        NetworkMockFieldView(
            label = "triggerProbability %",
            placeHolder = "0.0",
            value = editableState.latency.triggerProbability.toString(),
            onValueChange = {
                editableState = editableState.copy(
                    latency = editableState.latency.copy(
                        triggerProbability = it.toFloatOrNull() ?: 0f
                    )
                )
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NetworkMockFieldView(
                modifier = Modifier.weight(1f),
                label = "minLatencyMs",
                placeHolder = "0ms",
                value = editableState.latency.minLatencyMs.toString(),
                onValueChange = {
                    editableState = editableState.copy(
                        latency = editableState.latency.copy(
                            minLatencyMs = it.toLongOrNull() ?: 0
                        )
                    )
                }
            )
            NetworkMockFieldView(
                modifier = Modifier.weight(1f),
                label = "maxLatencyMs",
                placeHolder = "0ms",
                value = editableState.latency.maxLatencyMs.toString(),
                onValueChange = {
                    editableState = editableState.copy(
                        latency = editableState.latency.copy(
                            maxLatencyMs = it.toLongOrNull() ?: 0
                        )
                    )
                }
            )
        }

        ErrorsListView(
            errors = editableState.errors,
            onErrorsChange = { newErrors ->
                editableState = editableState.copy(errors = newErrors)
            }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable(onClick = close)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Close",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.panel,
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable(onClick = {
                        save(editableState)
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
    }
}

fun toEditableState(state: BadQualityConfigUiModel?): BadQualityConfigUiModel {
    return state ?: BadQualityConfigUiModel(
        isEnabled = true,
        latency = BadQualityConfigUiModel.LatencyConfig(
            triggerProbability = 0f,
            minLatencyMs = 0,
            maxLatencyMs = 0,
        ),
        errorProbability = 0.0,
        errors = emptyList(),
    )
}


@Composable
fun ErrorsListView(
    errors: List<BadQualityConfigUiModel.Error>,
    onErrorsChange: (List<BadQualityConfigUiModel.Error>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Errors", style = FloconTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable {
                        onErrorsChange(
                            errors + BadQualityConfigUiModel.Error(
                                weight = 1f,
                                httpCode = 500,
                                body = "",
                                contentType = "application/json"
                            )
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Add",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.panel,
                )
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(errors) { error ->
                Column(
                    modifier = Modifier
                        .width(230.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                        .padding(8.dp)
                ) {

                    val textStyle = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Thin,
                        color = FloconTheme.colorPalette.onSurface,
                    )

                    Text("Weight : ${error.weight}", style = textStyle)
                    Text("HttpCode : ${error.httpCode}", style = textStyle) // or throwable ?
                    Text("Cody : ${error.contentType}", style = textStyle)
                    Text("Body : ${error.body}", maxLines = 2, style = textStyle)

                    // bouton supprimer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete error",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onErrorsChange(errors.toMutableList().filterNot { it.uuid == error.uuid })
                                },
                            colorFilter = ColorFilter.tint(Color.Red)
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun ErrorsEditor(
    errors: List<BadQualityConfigUiModel.Error>,
    onErrorsChange: (List<BadQualityConfigUiModel.Error>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        errors.forEachIndexed { index, error ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                NetworkMockFieldView(
                    label = "Weight",
                    placeHolder = "1.0",
                    value = error.weight.toString(),
                    onValueChange = {
                        onErrorsChange(
                            errors.toMutableList().apply {
                                this[index] = this[index].copy(
                                    weight = it.toFloatOrNull() ?: 0f
                                )
                            }
                        )
                    }
                )

                NetworkMockFieldView(
                    label = "HTTP Code",
                    placeHolder = "500",
                    value = error.httpCode.toString(),
                    onValueChange = {
                        onErrorsChange(
                            errors.toMutableList().apply {
                                this[index] = this[index].copy(
                                    httpCode = it.toIntOrNull() ?: 500
                                )
                            }
                        )
                    }
                )

                NetworkMockFieldView(
                    label = "Content-Type",
                    placeHolder = "application/json",
                    value = error.contentType,
                    onValueChange = {
                        onErrorsChange(
                            errors.toMutableList().apply {
                                this[index] = this[index].copy(contentType = it)
                            }
                        )
                    }
                )

                NetworkMockFieldView(
                    label = "Body",
                    placeHolder = "{\"error\":\"...\"}",
                    value = error.body,
                    onValueChange = {
                        onErrorsChange(
                            errors.toMutableList().apply {
                                this[index] = this[index].copy(body = it)
                            }
                        )
                    }
                )

                // bouton supprimer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete error",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onErrorsChange(errors.toMutableList().apply { removeAt(index) })
                            },
                        colorFilter = ColorFilter.tint(Color.Red)
                    )
                }
            }
        }

        // bouton ajouter
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(FloconTheme.colorPalette.onSurface)
                .clickable {
                    onErrorsChange(
                        errors + BadQualityConfigUiModel.Error(
                            weight = 1f,
                            httpCode = 500,
                            body = "",
                            contentType = "application/json"
                        )
                    )
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                "Add Error",
                style = FloconTheme.typography.titleSmall,
                color = FloconTheme.colorPalette.panel,
            )
        }
    }
}

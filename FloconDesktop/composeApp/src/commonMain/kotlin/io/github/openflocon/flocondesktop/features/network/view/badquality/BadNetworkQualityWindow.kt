@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.view.badquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
        FloconSurface {
            BadNetworkQualityContent(
                state = state,
                save = viewModel::save,
                close = { onCloseRequest() },
            )
        }
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
    var isEnabled by remember(state) { mutableStateOf<Boolean>(state?.isEnabled ?: true) }
    var triggerProbability by remember(state) {
        mutableStateOf<String>(
            state?.latency?.triggerProbability?.let { it * 100.0 }?.toString() ?: "100"
        )
    }
    var minLatencyMs by remember(state) {
        mutableStateOf<String>(
            state?.latency?.minLatencyMs?.toString() ?: "0"
        )
    }
    var maxLatencyMs by remember(state) {
        mutableStateOf<String>(
            state?.latency?.maxLatencyMs?.toString() ?: "0"
        )
    }
    var errorProbability by remember(state) {
        mutableStateOf<String>(
            state?.errorProbability?.let { it * 100.0 }?.toString() ?: "100"
        )
    }
    var errors by remember(state) { mutableStateOf(state?.errors ?: emptyList()) }

    var selectedErrorToEdit by remember { mutableStateOf<BadQualityConfigUiModel.Error?>(null) }
    Column(
        modifier = modifier,
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
                    .clickable(onClick = close)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Cancel",
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
                        val minLatencyMsValue = minLatencyMs.toLong().coerceAtLeast(0)
                        val maxLatencyMsValue =
                            maxLatencyMs.toLong().coerceAtLeast(minLatencyMsValue)
                        save(
                            BadQualityConfigUiModel(
                                isEnabled = isEnabled,
                                latency = BadQualityConfigUiModel.LatencyConfig(
                                    triggerProbability = triggerProbability
                                        .toDoubleOrNull()
                                        ?.let { it / 100.0 }
                                        ?.coerceIn(0.0, 100.0)
                                        ?: 1.0,
                                    minLatencyMs = minLatencyMsValue,
                                    maxLatencyMs = maxLatencyMsValue,
                                ),
                                errorProbability = errorProbability
                                    .toDoubleOrNull()
                                    ?.let { it / 100.0 }
                                    ?.coerceIn(0.0, 100.0)
                                    ?: 1.0,
                                errors = errors,
                            )
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
        Column(
            modifier = Modifier.padding(
                all = 8.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MockNetworkLabelView("isEnabled")
                Switch(
                    modifier = Modifier.scale(0.6f),
                    checked = isEnabled,
                    onCheckedChange = {
                        isEnabled = it
                    },
                )
            }
            NetworkMockFieldView(
                label = "triggerProbability 0-100 %",
                placeHolder = "0",
                value = triggerProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        triggerProbability = it
                    }
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NetworkMockFieldView(
                    modifier = Modifier.weight(1f),
                    label = "minLatency (ms)",
                    placeHolder = "0ms",
                    value = minLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            minLatencyMs = it
                        }
                    }
                )
                NetworkMockFieldView(
                    modifier = Modifier.weight(1f),
                    label = "maxLatency (ms)",
                    placeHolder = "0ms",
                    value = maxLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            maxLatencyMs = it
                        }
                    }
                )
            }

            NetworkMockFieldView(
                label = "errorProbability 0-100 %",
                placeHolder = "0",
                value = errorProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        errorProbability = it
                    }
                }
            )

            ErrorsListView(
                modifier = Modifier.padding(top = 16.dp),
                errors = errors,
                onErrorsClicked = { error ->
                    selectedErrorToEdit = error
                },
                deleteError = { error ->
                    errors = errors.filterNot { it == error }
                }
            )

            selectedErrorToEdit?.let { selectedError ->
                BasicAlertDialog(
                    onDismissRequest = {
                        selectedErrorToEdit = null
                    }
                ) {
                    FloconSurface {
                        ErrorsEditor(
                            error = selectedError,
                            onErrorsChange = { error ->
                                errors = if (errors.any { it.uuid == selectedError.uuid }) {
                                    errors.map {
                                        if (it.uuid == selectedError.uuid) {
                                            error
                                        } else it
                                    }
                                } else {
                                    errors + error
                                }
                                selectedErrorToEdit = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorsListView(
    errors: List<BadQualityConfigUiModel.Error>,
    onErrorsClicked: (error: BadQualityConfigUiModel.Error) -> Unit,
    deleteError: (error: BadQualityConfigUiModel.Error) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Errors", style = FloconTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable {
                        onErrorsClicked(
                            BadQualityConfigUiModel.Error(
                                // new error
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(errors) { error ->
                Column(
                    modifier = Modifier
                        .width(230.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                        .clickable {
                            onErrorsClicked(error)
                        }
                        .padding(8.dp)
                ) {

                    val textStyle = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Thin,
                        color = FloconTheme.colorPalette.onSurface,
                    )

                    Text("Weight : ${error.weight}", style = textStyle)
                    Text("HttpCode : ${error.httpCode}", style = textStyle) // or throwable ?
                    Text(error.contentType, style = textStyle)
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
                                    deleteError(
                                        error
                                    )
                                },
                            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface)
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun ErrorsEditor(
    error: BadQualityConfigUiModel.Error,
    onErrorsChange: (BadQualityConfigUiModel.Error) -> Unit,
) {
    var weight by remember(error) { mutableStateOf<String>(error.weight.toString()) }
    var httpCode by remember(error) { mutableStateOf<String>(error.httpCode.toString()) }
    var contentType by remember() { mutableStateOf<String>(error.contentType) }
    var body by remember(error) { mutableStateOf<String>(error.body) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // bouton ajouter
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(FloconTheme.colorPalette.onSurface)
                .clickable {
                    onErrorsChange(
                        error.copy(
                            weight = weight.toFloatOrNull() ?: error.weight,
                            httpCode = httpCode.toIntOrNull() ?: error.httpCode,
                            contentType = contentType,
                            body = body
                        )
                    )
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                "Save",
                style = FloconTheme.typography.titleSmall,
                color = FloconTheme.colorPalette.panel,
            )
        }
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                .padding(8.dp)
        ) {
            NetworkMockFieldView(
                label = "Weight",
                placeHolder = "eg: 1.0",
                value = weight,
                onValueChange = {
                    if (it.isEmpty() || it.toFloatOrNull() != null) {
                        weight = it
                    }
                }
            )

            NetworkMockFieldView(
                label = "HTTP Code",
                placeHolder = "eg: 500",
                value = httpCode,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        httpCode = it
                    }
                }
            )

            NetworkMockFieldView(
                label = "Content-Type",
                placeHolder = "application/json",
                value = contentType,
                onValueChange = {
                    contentType = it
                }
            )

            NetworkMockFieldView(
                label = "Body",
                placeHolder = "{\"error\":\"...\"}",
                value = body,
                onValueChange = {
                    body = it
                }
            )
        }
    }
}

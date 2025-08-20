@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.NetworkMockFieldView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconSurface
import java.util.UUID

@Composable
fun BadQualityEditionWindow(
    onCloseRequest: () -> Unit,
    save: (state: BadQualityConfigUiModel) -> Unit,
    state: SelectedBadQualityUiModel,
) {
    FloconDialog(
        onDismissRequest = onCloseRequest
    ) {
        BadNetworkQualityEditionContent(
            state = state,
            save = save,
            close = onCloseRequest,
        )
    }
}

@Composable
fun BadNetworkQualityEditionContent(
    state: SelectedBadQualityUiModel,
    close: () -> Unit,
    save: (state: BadQualityConfigUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = state.config

    var name by remember(state) {
        mutableStateOf<String>(
            config?.name ?: ""
        )
    }
    var triggerProbability by remember(state) {
        mutableStateOf<String>(
            config?.latency?.triggerProbability?.let { it * 100.0 }?.toString() ?: "100"
        )
    }
    var minLatencyMs by remember(state) {
        mutableStateOf<String>(
            config?.latency?.minLatencyMs?.toString() ?: "0"
        )
    }
    var maxLatencyMs by remember(state) {
        mutableStateOf<String>(
            config?.latency?.maxLatencyMs?.toString() ?: "0"
        )
    }
    var errorProbability by remember(state) {
        mutableStateOf<String>(
            config?.errorProbability?.let { it * 100.0 }?.toString() ?: "100"
        )
    }
    var errors by remember(state) { mutableStateOf(config?.errors ?: emptyList()) }
    var selectedErrorToEdit by remember { mutableStateOf<BadQualityConfigUiModel.Error?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        NetworkMockFieldView(
            label = "Name",
            placeHolder = "",
            value = name,
            onValueChange = {
                name = it
            },
            trailingComponent = {  }
        )
        NetworkMockFieldView(
            label = "Trigger probability",
            placeHolder = "0",
            value = triggerProbability,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull() != null) {
                    triggerProbability = it
                }
            },
            trailingComponent = { Text(text = "%") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NetworkMockFieldView(
                modifier = Modifier.weight(1f),
                label = "Min latency",
                placeHolder = "0",
                value = minLatencyMs,
                onValueChange = {
                    if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                        minLatencyMs = it
                    }
                },
                trailingComponent = { Text(text = "ms") }
            )
            NetworkMockFieldView(
                modifier = Modifier.weight(1f),
                label = "Max latency",
                placeHolder = "0",
                value = maxLatencyMs,
                onValueChange = {
                    if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                        maxLatencyMs = it
                    }
                },
                trailingComponent = { Text(text = "ms") }
            )
        }
        NetworkMockFieldView(
            label = "Error probability",
            placeHolder = "0",
            value = errorProbability,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull() != null) {
                    errorProbability = it
                }
            },
            trailingComponent = { Text(text = "%") }
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
        Spacer(Modifier.weight(1f))
        FloconDialogButtons(
            onCancel = close,
            onValidate = {
                val minLatencyMsValue = minLatencyMs.toLong().coerceAtLeast(0)
                val maxLatencyMsValue =
                    maxLatencyMs.toLong().coerceAtLeast(minLatencyMsValue)
                save(
                    BadQualityConfigUiModel(
                        id = config?.id ?: UUID.randomUUID().toString(), // generate a new one
                        name = name,
                        isEnabled = config?.isEnabled ?: false, // disabled by default
                        createdAt = config?.createdAt ?: System.currentTimeMillis(), // generate a new date
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )
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
            FloconButton(
                onClick = {
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
            ) {
                Text(
                    text = "Add"
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
    var contentType by remember { mutableStateOf<String>(error.contentType) }
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

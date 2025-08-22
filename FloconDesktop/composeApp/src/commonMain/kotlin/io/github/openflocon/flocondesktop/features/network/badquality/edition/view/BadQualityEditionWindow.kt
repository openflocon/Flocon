@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import java.util.UUID

@Composable
fun BadQualityEditionWindow(
    onCloseRequest: () -> Unit,
    save: (state: BadQualityConfigUiModel) -> Unit,
    state: SelectedBadQualityUiModel,
) {
    FloconDialog(
        onDismissRequest = onCloseRequest,
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
            config?.name ?: "",
        )
    }
    var triggerProbability by remember(state) {
        mutableStateOf<String>(
            config?.latency?.triggerProbability?.let { it * 100.0 }?.toString() ?: "100",
        )
    }
    var minLatencyMs by remember(state) {
        mutableStateOf<String>(
            config?.latency?.minLatencyMs?.toString() ?: "0",
        )
    }
    var maxLatencyMs by remember(state) {
        mutableStateOf<String>(
            config?.latency?.maxLatencyMs?.toString() ?: "0",
        )
    }
    var errorProbability by remember(state) {
        mutableStateOf<String>(
            config?.errorProbability?.let { it * 100.0 }?.toString() ?: "100",
        )
    }
    var errors by remember(state) { mutableStateOf(config?.errors ?: emptyList()) }
    var selectedErrorToEdit by remember { mutableStateOf<BadQualityConfigUiModel.Error?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            FloconTextField(
                label = defaultLabel("Name"),
                value = name,
                onValueChange = { name = it },
                suffix = { },
                modifier = Modifier.fillMaxWidth(fraction = 0.5f),
                containerColor = FloconTheme.colorPalette.panel,
            )
            FloconTextField(
                label = defaultLabel("Trigger probability"),
                placeholder = defaultPlaceHolder("0"),
                value = triggerProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        triggerProbability = it
                    }
                },
                suffix = { Text(text = "%") },
                containerColor = FloconTheme.colorPalette.panel
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FloconTextField(
                    modifier = Modifier.weight(1f),
                    label = defaultLabel("Min latency"),
                    placeholder = defaultPlaceHolder("0"),
                    value = minLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            minLatencyMs = it
                        }
                    },
                    suffix = { Text(text = "ms") },
                    containerColor = FloconTheme.colorPalette.panel,
                )
                FloconTextField(
                    modifier = Modifier.weight(1f),
                    label = defaultLabel("Max latency"),
                    placeholder = defaultPlaceHolder("0"),
                    value = maxLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            maxLatencyMs = it
                        }
                    },
                    suffix = { Text(text = "ms") },
                    containerColor = FloconTheme.colorPalette.panel,
                )
            }
            FloconTextField(
                label = defaultLabel("Error probability"),
                placeholder = defaultPlaceHolder("0"),
                value = errorProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        errorProbability = it
                    }
                },
                suffix = { Text(text = "%") },
                containerColor = FloconTheme.colorPalette.panel,
            )
        }
        BadQualityErrorsListView(
            modifier = Modifier.padding(top = 16.dp),
            errors = errors,
            onErrorslicked = { error ->
                selectedErrorToEdit = error
            },
            deleteError = { error ->
                errors = errors.filterNot { it == error }
            },
        )
        selectedErrorToEdit?.let { selectedError ->
            FloconDialog(
                onDismissRequest = {
                    selectedErrorToEdit = null
                },
            ) {
                FloconSurface {
                    when (val t = selectedError.type) {
                        is BadQualityConfigUiModel.Error.Type.Body -> {
                            BadQualityErrorsEditor(
                                error = selectedError,
                                httpType = t,
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
                                },
                            )
                        }

                        is BadQualityConfigUiModel.Error.Type.Exception -> {
                            BadQuaityErrorExceptionEditor(
                                error = selectedError,
                                errorException = t,
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
                                },
                            )
                        }
                    }
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
                        createdAt = config?.createdAt
                            ?: System.currentTimeMillis(), // generate a new date
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
                    ),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        )
    }
}


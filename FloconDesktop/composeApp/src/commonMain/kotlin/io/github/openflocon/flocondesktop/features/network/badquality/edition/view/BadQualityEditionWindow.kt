@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import io.github.openflocon.library.designsystem.components.FloconActionButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import java.util.UUID
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.badquality_creation_subtitle
import flocondesktop.composeapp.generated.resources.badquality_error_probability
import flocondesktop.composeapp.generated.resources.badquality_max_latency
import flocondesktop.composeapp.generated.resources.badquality_min_latency
import flocondesktop.composeapp.generated.resources.badquality_trigger_probability
import flocondesktop.composeapp.generated.resources.general_cancel
import flocondesktop.composeapp.generated.resources.general_save
import flocondesktop.composeapp.generated.resources.mock_expectation
import flocondesktop.composeapp.generated.resources.name
import flocondesktop.composeapp.generated.resources.network_response

@Composable
fun BadQualityEditionWindow(
    onCloseRequest: () -> Unit,
    save: (state: BadQualityConfigUiModel) -> Unit,
    state: SelectedBadQualityUiModel,
) {
    FloconDialog(
        onDismissRequest = onCloseRequest,
        // two cards side by side: the default width of a dialog is too narrow
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.width(920.dp),
    ) {
        BadNetworkQualityEditionContent(
            state = state,
            save = save,
            close = onCloseRequest,
        )
    }
}

fun SelectedBadQualityUiModel.title(): String = when (this) {
    SelectedBadQualityUiModel.Creation -> "Creation"
    is SelectedBadQualityUiModel.Edition -> "Edition"
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
    var errors by remember(state) { mutableStateOf(config?.errors?.toPersistentList() ?: persistentListOf()) }
    var selectedErrorToEdit by remember { mutableStateOf<BadQualityConfigUiModel.Error?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .fillMaxWidth()
            .background(FloconTheme.colors.bg100),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large)
                .padding(top = FloconTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        ) {
            Text(
                text = state.title(),
                style = FloconTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
            )
            Text(
                text = stringResource(Res.string.badquality_creation_subtitle),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.inkMuted,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.small),
        ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .weight(1f)
                .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.large)
                .padding(FloconTheme.spacing.large),
        ) {
            Text(
                text = stringResource(Res.string.mock_expectation),
                style = FloconTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
            )
            FloconTextField(
                label = defaultLabel(stringResource(Res.string.name)),
                value = name,
                onValueChange = { name = it },
                suffix = { },
                modifier = Modifier.fillMaxWidth(),
                containerColor = FloconTheme.colors.bg200,
            )
            FloconTextField(
                label = defaultLabel(stringResource(Res.string.badquality_trigger_probability)),
                placeholder = defaultPlaceHolder("0"),
                value = triggerProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        triggerProbability = it
                    }
                },
                suffix = { Text(text = "%") },
                containerColor = FloconTheme.colors.bg200
            )
            Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
                FloconTextField(
                    modifier = Modifier.weight(1f),
                    label = defaultLabel(stringResource(Res.string.badquality_min_latency)),
                    placeholder = defaultPlaceHolder("0"),
                    value = minLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            minLatencyMs = it
                        }
                    },
                    suffix = { Text(text = "ms") },
                    containerColor = FloconTheme.colors.bg200,
                )
                FloconTextField(
                    modifier = Modifier.weight(1f),
                    label = defaultLabel(stringResource(Res.string.badquality_max_latency)),
                    placeholder = defaultPlaceHolder("0"),
                    value = maxLatencyMs,
                    onValueChange = {
                        if (it.isEmpty() || it.toLongOrNull()?.takeIf { it >= 0L } != null) {
                            maxLatencyMs = it
                        }
                    },
                    suffix = { Text(text = "ms") },
                    containerColor = FloconTheme.colors.bg200,
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .weight(1.35f)
                .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.large)
                .padding(FloconTheme.spacing.large),
        ) {
            Text(
                text = stringResource(Res.string.network_response),
                style = FloconTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = FloconTheme.colors.ink,
            )
            FloconTextField(
                label = defaultLabel(stringResource(Res.string.badquality_error_probability)),
                placeholder = defaultPlaceHolder("0"),
                value = errorProbability,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                        errorProbability = it
                    }
                },
                suffix = { Text(text = "%") },
                containerColor = FloconTheme.colors.bg200,
            )
        BadQualityErrorsListView(
            modifier = Modifier
                .padding(top = FloconTheme.spacing.medium),
            errors = errors,
            onErrorslicked = { error ->
                selectedErrorToEdit = error
            },
            deleteError = { error ->
                errors = errors.filterNot { it == error }.toPersistentList()
            },
        )
        }
        }
        selectedErrorToEdit?.let { selectedError ->
            FloconDialog(
                onDismissRequest = {
                    selectedErrorToEdit = null
                },
            ) {
                FloconSurface {
                    when (val t = selectedError.type) {
                        is BadQualityConfigUiModel.Error.Type.Body -> {
                            BadQualityHttpErrorEditor(
                                error = selectedError,
                                httpType = t,
                                cancel = {
                                    selectedErrorToEdit = null
                                },
                                save = { error ->
                                    errors = if (errors.any { it.uuid == selectedError.uuid }) {
                                        errors.map {
                                            if (it.uuid == selectedError.uuid) {
                                                error
                                            } else it
                                        }.toPersistentList()
                                    } else {
                                        errors.adding(error)
                                    }
                                    selectedErrorToEdit = null
                                },
                            )
                        }

                        is BadQualityConfigUiModel.Error.Type.Exception -> {
                            BadQuaityErrorExceptionEditor(
                                error = selectedError,
                                errorException = t,
                                cancel = {
                                    selectedErrorToEdit = null
                                },
                                save = { error ->
                                    errors = if (errors.any { it.uuid == selectedError.uuid }) {
                                        errors.map {
                                            if (it.uuid == selectedError.uuid) {
                                                error
                                            } else it
                                        }.toPersistentList()
                                    } else {
                                        errors.adding(error)
                                    }
                                    selectedErrorToEdit = null
                                },
                            )
                        }
                    }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(FloconTheme.colors.line)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large, vertical = FloconTheme.spacing.small),
        ) {
        FloconActionButton(onClick = close) {
            Text(stringResource(Res.string.general_cancel))
        }
        FloconActionButton(
            primary = true,
            onClick = {
                val minLatencyMsValue = minLatencyMs.toLong().coerceAtLeast(0)
                val maxLatencyMsValue =
                    maxLatencyMs.toLong().coerceAtLeast(minLatencyMsValue)
                save(
                    BadQualityConfigUiModel(
                        id = config?.id ?: UUID.randomUUID().toString(), // generate a new one
                        name = name.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(), // generate if empty, TODO : fail
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
        ) {
            Text(stringResource(Res.string.general_save))
        }
        }
    }
}

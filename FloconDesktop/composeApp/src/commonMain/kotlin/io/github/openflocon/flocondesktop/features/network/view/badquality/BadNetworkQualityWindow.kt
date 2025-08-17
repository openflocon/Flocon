@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.view.badquality

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.model.badquality.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.view.mocks.NetworkMockFieldView
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BadNetworkQualityWindow(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BadQualityNetworkViewModel = koinViewModel()
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    BasicAlertDialog(
        onDismissRequest = onCloseRequest,
    ) {
        BadNetworkQualityContent(
            state = state,
            save = viewModel::save,
        )
    }
}

@Composable
fun BadNetworkQualityContent(
    state: BadQualityConfigUiModel?,
    save: (state: BadQualityConfigUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editableState: BadQualityConfigUiModel = remember(state) { toEditableState(state) }
    Column(
        modifier = modifier,
    ) {

        Column {
            Text("isEnabled")
            Switch(
                modifier = Modifier.scale(0.6f),
                checked = editableState.isEnabled,
                onCheckedChange = {
                    editableState.copy(isEnabled = it)
                },
            )
        }
        NetworkMockFieldView(
            label = "triggerProbability",
            placeHolder = "0.0",
            value = editableState.latency.triggerProbability.toString(),
            onValueChange = {
                editableState.latency.copy(
                    triggerProbability = it.toFloatOrNull() ?: 0f
                )
            }
        )
        NetworkMockFieldView(
            label = "minLatencyMs",
            placeHolder = "0ms",
            value = editableState.latency.minLatencyMs.toString(),
            onValueChange = {
                editableState.latency.copy(
                    minLatencyMs = it.toLongOrNull() ?: 0
                )
            }
        )
        NetworkMockFieldView(
            label = "maxLatencyMs",
            placeHolder = "0ms",
            value = editableState.latency.maxLatencyMs.toString(),
            onValueChange = {
                editableState.latency.copy(
                    maxLatencyMs = it.toLongOrNull() ?: 0
                )
            }
        )

        // TODO errors

        Box(
            modifier = Modifier
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

fun toEditableState(state: BadQualityConfigUiModel?) : BadQualityConfigUiModel {
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

package io.github.openflocon.flocondesktop.device.pages.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconTextValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun InfoPage(
    deviceSerial: String
) {
    val viewModel = koinViewModel<InfoViewModel> { parametersOf(deviceSerial) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: InfoUiState,
    onAction: (InfoAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        General(uiState)
    }
}

@Composable
private fun General(
    state: InfoUiState
) {
    Section(
        title = "General"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            FloconTextValue(
                label = "Brand",
                value = state.brand,
                valueContainerColor = FloconTheme.colors.bg300
            )
            FloconTextValue(
                label = "Battery",
                value = state.battery,
                valueContainerColor = FloconTheme.colors.bg300
            )
            FloconTextValue(
                label = "Serial number",
                value = state.serialNumber,
                valueContainerColor = FloconTheme.colors.bg300
            )
            FloconTextValue(
                label = "Version - Release",
                value = state.versionRelease,
                valueContainerColor = FloconTheme.colors.bg300
            )
            FloconTextValue(
                label = "Version - Sdk",
                value = state.versionSdk,
                valueContainerColor = FloconTheme.colors.bg300
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    FloconSection(
        title = title,
        initialValue = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colors.line)
            .border(
                width = 1.dp,
                color = FloconTheme.colors.bg300,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(8.dp),
            content = content
        )
    }
}

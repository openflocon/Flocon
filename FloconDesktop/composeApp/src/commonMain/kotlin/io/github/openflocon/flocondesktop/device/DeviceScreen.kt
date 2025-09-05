package io.github.openflocon.flocondesktop.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconScrollableTabRow
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.FloconTextValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun DeviceScreen(
    deviceId: DeviceId,
    onCloseRequest: () -> Unit
) {
    val viewModel = koinViewModel<DeviceViewModel> {
        parametersOf(deviceId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onCloseRequest = onCloseRequest,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: DeviceUiState,
    onCloseRequest: () -> Unit,
    onAction: (DeviceAction) -> Unit
) {
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(uiState.selectedIndex) {
        pagerState.animateScrollToPage(uiState.selectedIndex)
    }

    FloconWindow(
        title = "Device",
        onCloseRequest = onCloseRequest,
        state = createFloconWindowState()
    ) {
//        window.minimumSize = Dimension(500, 500) // TODO
        FloconSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    Column(
                        modifier = Modifier.background(FloconTheme.colorPalette.primary.copy(alpha = 1f))
                    ) {
                        Header(
                            uiState = uiState,
                            onAction = onAction
                        )
                        FloconScrollableTabRow(
                            selectedTabIndex = uiState.selectedIndex
                        ) {
                            FloconTab(
                                text = "Info",
                                selected = true,
                                onClick = { onAction(DeviceAction.SelectTab(0)) }
                            )
                            FloconTab(
                                text = "Permission",
                                selected = true,
                                onClick = { onAction(DeviceAction.SelectTab(1)) }
                            )
                        }
                    }
                },
                containerColor = FloconTheme.colorPalette.primary
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(it)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false
                    ) { index ->
                        when (index) {
                            0 -> InfoPage(uiState)
                            1 -> PermissionPage()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    uiState: DeviceUiState,
    onAction: (DeviceAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${uiState.model} (${uiState.serialNumber})",
            style = FloconTheme.typography.headlineSmall,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        )
        FloconIconButton(
            imageVector = Icons.Outlined.Refresh,
            onClick = { onAction(DeviceAction.Refresh) }
        )
    }
}

@Composable
private fun InfoPage(
    uiState: DeviceUiState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloconTextValue("Brand", uiState.brand)
        FloconTextValue("CPU", uiState.cpu)
        FloconTextValue("Memory", uiState.mem)
        FloconTextValue("Battery", uiState.battery)
        FloconTextValue("Serial number", uiState.serialNumber)
        FloconTextValue("Version - Release", uiState.versionRelease)
        FloconTextValue("Version - Sdk", uiState.versionSdk)
    }
}

@Composable
private fun PermissionPage() {

}

@Composable
@Preview
private fun Preview() {
    FloconTheme {
        Content(
            uiState = previewDeviceUiState(),
            onCloseRequest = {},
            onAction = {}
        )
    }
}

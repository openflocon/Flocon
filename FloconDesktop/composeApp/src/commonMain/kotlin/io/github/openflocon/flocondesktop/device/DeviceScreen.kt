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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.rememberFloconWindowState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconScrollableTabRow
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTab
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.awt.Dimension

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
        state = rememberFloconWindowState()
    ) {
        window.minimumSize = Dimension(500, 500) // TODO
        FloconSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    Column(
                        modifier = Modifier.background(FloconTheme.colorPalette.surfaceVariant)
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
                containerColor = FloconTheme.colorPalette.panel
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
            modifier = Modifier.weight(1f)
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
    Column {
        TextValue("Brand", uiState.brand)
        TextValue("CPU", uiState.cpu)
        TextValue("MEM", uiState.mem)
        TextValue("Battery", uiState.battery)
        TextValue("SerialNumber", uiState.serialNumber)
        TextValue("VersionRelease", uiState.versionRelease)
        TextValue("VersionSdk", uiState.versionSdk)
    }
}

@Composable
private fun PermissionPage() {

}

@Composable
private fun TextValue(
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.labelSmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = FloconTheme.typography.labelSmall,
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(FloconTheme.colorPalette.surfaceVariant)
                .padding(2.dp)
        )
    }
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

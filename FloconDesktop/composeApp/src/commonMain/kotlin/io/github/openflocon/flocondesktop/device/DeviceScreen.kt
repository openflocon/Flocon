package io.github.openflocon.flocondesktop.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import io.github.openflocon.flocondesktop.common.ui.window.rememberFloconWindowState
import io.github.openflocon.library.designsystem.FloconTheme
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
            Column {
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

@Composable
private fun InfoPage(
    uiState: DeviceUiState
) {
    Column {
        TextValue("Model:", uiState.model)
        TextValue("Brand:", uiState.brand)
        TextValue("CPU:", uiState.cpu)
        TextValue("MEM:", uiState.mem)
        TextValue("Battery:", uiState.battery)
        TextValue("SerialNumber:", uiState.serialNumber)
        TextValue("VersionRelease:", uiState.versionRelease)
        TextValue("VersionSdk:", uiState.versionSdk)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label
        )
        Text(
            text = value
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

package io.github.openflocon.flocondesktop.device

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
import io.github.openflocon.flocondesktop.device.models.previewDeviceUiState
import io.github.openflocon.flocondesktop.device.pages.BatteryPage
import io.github.openflocon.flocondesktop.device.pages.cpu.CpuPage
import io.github.openflocon.flocondesktop.device.pages.InfoPage
import io.github.openflocon.flocondesktop.device.pages.MemoryPage
import io.github.openflocon.flocondesktop.device.pages.permission.PermissionPage
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconScaffold
import io.github.openflocon.library.designsystem.components.FloconScrollableTabRow
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTab
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun DeviceScreen(
    deviceId: DeviceId
) {
    val viewModel = koinViewModel<DeviceViewModel> {
        parametersOf(deviceId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: DeviceUiState,
    onAction: (DeviceAction) -> Unit
) {
    val tabs = remember { DeviceTab.entries }
    val pagerState = rememberPagerState { tabs.size }

    LaunchedEffect(uiState.contentState.selectedTab) {
        pagerState.animateScrollToPage(uiState.contentState.selectedTab.ordinal)
    }

    FloconSurface(
        modifier = Modifier.fillMaxSize()
    ) {
        FloconScaffold(
            topBar = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Header(
                        uiState = uiState,
                        onAction = onAction
                    )
                    FloconScrollableTabRow(
                        selectedTabIndex = uiState.contentState.selectedTab.ordinal,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEach { tab ->
                            FloconTab(
                                text = tab.title,
                                selected = uiState.contentState.selectedTab == tab,
                                onClick = { onAction(DeviceAction.SelectTab(tab)) },
                                selectedContentColor = FloconTheme.colorPalette.onSurface
                            )
                        }
                    }
                    FloconHorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = FloconTheme.colorPalette.primary
                    )
                }
            }
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                contentPadding = PaddingValues(8.dp),
                pageSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) { index ->
                when (tabs[index]) {
                    DeviceTab.INFORMATION -> InfoPage(uiState.infoState)
                    DeviceTab.BATTERY -> BatteryPage(uiState.batteryState)
                    DeviceTab.CPU -> CpuPage(uiState.deviceSerial)
                    DeviceTab.MEMORY -> MemoryPage(uiState.memoryState)
                    DeviceTab.PERMISSION -> PermissionPage(uiState.deviceSerial)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f)
        ) {
            Text(
                text = uiState.infoState.model,
                style = FloconTheme.typography.headlineSmall
            )
            SelectionContainer {
                Text(
                    text = uiState.infoState.serialNumber,
                    style = FloconTheme.typography.labelSmall
                )
            }
        }
        FloconIconButton(
            imageVector = Icons.Outlined.Refresh,
            onClick = { onAction(DeviceAction.Refresh) }
        )
    }
}

@Composable
@Preview
private fun Preview() {
    FloconTheme {
        Content(
            uiState = previewDeviceUiState(),
            onAction = {}
        )
    }
}

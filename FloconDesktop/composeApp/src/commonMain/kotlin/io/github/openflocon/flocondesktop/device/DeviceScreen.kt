package io.github.openflocon.flocondesktop.device

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
import io.github.openflocon.flocondesktop.device.models.previewDeviceUiState
import io.github.openflocon.flocondesktop.device.pages.CpuPage
import io.github.openflocon.flocondesktop.device.pages.InfoPage
import io.github.openflocon.flocondesktop.device.pages.PermissionPage
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconScaffold
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
    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(uiState.contentState.selectedIndex) {
        pagerState.animateScrollToPage(uiState.contentState.selectedIndex)
    }

    FloconWindow(
        title = "Device - ${uiState.infoState.model}",
        onCloseRequest = onCloseRequest,
        state = createFloconWindowState()
    ) {
//        window.minimumSize = Dimension(500, 500) // TODO
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
                            selectedTabIndex = uiState.contentState.selectedIndex,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FloconTab(
                                text = "Info",
                                selected = uiState.contentState.selectedIndex == 0,
                                onClick = { onAction(DeviceAction.SelectTab(0)) },
                                selectedContentColor = FloconTheme.colorPalette.onSurface
                            )
                            FloconTab(
                                text = "Cpu",
                                selected = uiState.contentState.selectedIndex == 1,
                                onClick = { onAction(DeviceAction.SelectTab(1)) },
                                selectedContentColor = FloconTheme.colorPalette.onSurface
                            )
                            FloconTab(
                                text = "Permission",
                                selected = uiState.contentState.selectedIndex == 2,
                                onClick = { onAction(DeviceAction.SelectTab(2)) },
                                selectedContentColor = FloconTheme.colorPalette.onSurface
                            )
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
                    when (index) {
                        0 -> InfoPage(uiState.infoState)
                        1 -> CpuPage(
                            state = uiState.cpuState,
                            onAction = onAction
                        )

                        2 -> PermissionPage(
                            state = uiState.permissionState,
                            onAction = onAction
                        )

                        else -> Unit
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
            onCloseRequest = {},
            onAction = {}
        )
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.menu.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.flocondesktop.features.dashboard.view.DashboardScreen
import io.github.openflocon.flocondesktop.features.database.view.DatabaseScreen
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkScreen
import io.github.openflocon.flocondesktop.features.files.view.FilesScreen
import io.github.openflocon.flocondesktop.features.images.view.ImagesScreen
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.SharedPreferencesScreen
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.flocondesktop.menu.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.menu.ui.settings.SettingsScreen
import io.github.openflocon.flocondesktop.menu.ui.view.leftpannel.LeftPanelView
import io.github.openflocon.flocondesktop.menu.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.menu.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.flocondesktop.menu.ui.view.topbar.MainScreenTopBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.navigation.FloconNavigation
import org.koin.compose.currentKoinScope
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = currentKoinScope().get<MainViewModel>()
    val leftPanelState by viewModel.leftPanelState.collectAsStateWithLifecycle()
    val devicesState by viewModel.devicesState.collectAsStateWithLifecycle()
    val appsState by viewModel.appsState.collectAsStateWithLifecycle()
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()

    MenuScreen(
        navigationState = viewModel.menuNavigationState,
        modifier = modifier,
        devicesState = devicesState,
        appsState = appsState,
        recordState = recordState,
        onDeviceSelected = viewModel::onDeviceSelected,
        deleteDevice = viewModel::deleteDevice,
        deleteApp = viewModel::deleteApp,
        onAppSelected = viewModel::onAppSelected,
        leftPanelState = leftPanelState,
        onClickLeftPanelItem = viewModel::onClickLeftPanelItem,
        onTakeScreenshotClicked = viewModel::onTakeScreenshotClicked,
        onRecordClicked = viewModel::onRecordClicked,
        onRestartClicked = viewModel::onRestartClicked,
    )
}

@Composable
private fun MenuScreen(
    navigationState: MenuNavigationState,
    leftPanelState: LeftPanelState,
    onClickLeftPanelItem: (LeftPanelItem) -> Unit,
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
    onTakeScreenshotClicked: () -> Unit,
    recordState: RecordVideoStateUiModel,
    onRecordClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(true) }
    val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)
    var windowSize by remember { mutableStateOf(IntSize.Zero) }
    val position by animateDpAsState(
        targetValue = if (expanded) PanelMaxWidth else PanelMinWidth,
    )
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        MainScreenTopBar(
            appsState = appsState,
            devicesState = devicesState,
            recordState = recordState,
            deleteApp = deleteApp,
            deleteDevice = deleteDevice,
            onAppSelected = onAppSelected,
            onRecordClicked = onRecordClicked,
            onRestartClicked = onRestartClicked,
            onDeviceSelected = onDeviceSelected,
            onTakeScreenshotClicked = onTakeScreenshotClicked
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LeftPanelView(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight(),
                expanded = expanded,
                onClickItem = onClickLeftPanelItem,
                state = leftPanelState,
            )
            Spacer(Modifier.width(8.dp))
            FloconNavigation(
                navigationState = navigationState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .onGloballyPositioned {
                        windowSize = it.size // TODO Add windowsize lib
                    },
            ) {
                menus()
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(20.dp)
                    .height(60.dp)
                    .graphicsLayer {
                        translationX = position.toPx() - size.width / 2 - 8.dp.toPx()
                        translationY = (windowSize.height / 2) - (size.height / 2)
                    }
                    .clip(RoundedCornerShape(4.dp))
                    .background(FloconTheme.colorPalette.primary)
                    .clickable(onClick = { expanded = !expanded }),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.ChevronRight,
                    tint = Color.LightGray,
                    modifier = Modifier.rotate(rotate),
                )
            }
        }
    }
}

private fun EntryProviderBuilder<in SubScreen>.menus() {
    entry<SubScreen.Network> { NetworkScreen() }
    entry<SubScreen.Dashboard> { DashboardScreen() }
    entry<SubScreen.Analytics> { AnalyticsScreen() }
    entry<SubScreen.Tables> { TableScreen() }
    entry<SubScreen.Images> { ImagesScreen() }
    entry<SubScreen.Files> { FilesScreen() }
    entry<SubScreen.SharedPreferences> { SharedPreferencesScreen() }
    entry<SubScreen.Database> { DatabaseScreen() }
    entry<SubScreen.Deeplinks> { DeeplinkScreen() }
    entry<SubScreen.Settings> { SettingsScreen() }
}

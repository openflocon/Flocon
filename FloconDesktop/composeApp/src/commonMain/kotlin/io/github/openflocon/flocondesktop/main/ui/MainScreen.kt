@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.main.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.flocondesktop.features.dashboard.view.DashboardScreen
import io.github.openflocon.flocondesktop.features.database.view.DatabaseScreen
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkScreen
import io.github.openflocon.flocondesktop.features.files.view.FilesScreen
import io.github.openflocon.flocondesktop.features.images.view.ImagesScreen
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.SharedPreferencesScreen
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.flocondesktop.main.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.settings.SettingsScreen
import io.github.openflocon.flocondesktop.main.ui.view.topbar.MainScreenTopBar
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.LeftPanelView
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.PanelMaxWidth
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.PanelMinWidth
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = koinViewModel()
    val leftPanelState by viewModel.leftPanelState.collectAsStateWithLifecycle()
    val subScreen by viewModel.subScreen.collectAsStateWithLifecycle()
    val devicesState by viewModel.devicesState.collectAsStateWithLifecycle()
    val appsState by viewModel.appsState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        MainScreen(
            subScreen = subScreen,
            modifier = Modifier.fillMaxSize(),
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = viewModel::onDeviceSelected,
            onAppSelected = viewModel::onAppSelected,
            leftPanelState = leftPanelState,
            onClickLeftPanelItem = viewModel::onClickLeftPanelItem,
        )
    }
}

@Composable
private fun MainScreen(
    subScreen: SubScreen,
    leftPanelState: LeftPanelState,
    onClickLeftPanelItem: (LeftPanelItem) -> Unit,
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(true) }
    val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)
    var windowSize by remember { mutableStateOf(IntSize.Zero) }
    val position by animateDpAsState(
        targetValue = if (expanded) PanelMaxWidth else PanelMinWidth,
    )
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Column(modifier = modifier) {
        MainScreenTopBar(
            modifier = Modifier
                .fillMaxWidth(),
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = onDeviceSelected,
            onAppSelected = onAppSelected,
        )
        Box(
            modifier = Modifier.fillMaxSize().onGloballyPositioned {
                windowSize = it.size // TODO Add windowsize lib
            },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                LeftPanelView(
                    modifier = Modifier
                        .width(width)
                        .fillMaxHeight(),
                    expanded = expanded,
                    onClickItem = onClickLeftPanelItem,
                    state = leftPanelState,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(6.dp),
                ) {
                    when (subScreen) {
                        SubScreen.Network ->
                            NetworkScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Database ->
                            DatabaseScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Images ->
                            ImagesScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Files ->
                            FilesScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Tables ->
                            TableScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.SharedPreferences ->
                            SharedPreferencesScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Dashboard ->
                            DashboardScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                            )

                        SubScreen.Settings -> {
                            SettingsScreen(
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        SubScreen.Deeplinks -> {
                            DeeplinkScreen(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            )
                        }

                        SubScreen.Analytics -> {
                            AnalyticsScreen(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            )
                        }
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(20.dp)
                    .height(60.dp)
                    .graphicsLayer {
                        translationX = position.toPx() - size.width / 2
                        translationY = (windowSize.height / 2) - (size.height / 2)
                    }
                    .clip(RoundedCornerShape(4.dp))
                    .background(FloconTheme.colorPalette.panel)
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

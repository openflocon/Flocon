@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.main.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.features.analytics.ui.view.AnalyticsScreen
import io.github.openflocon.flocondesktop.features.dashboard.ui.view.DashboardScreen
import io.github.openflocon.flocondesktop.features.database.ui.view.DatabaseScreen
import io.github.openflocon.flocondesktop.features.deeplinks.ui.view.DeeplinkScreen
import io.github.openflocon.flocondesktop.features.files.ui.view.FilesScreen
import io.github.openflocon.flocondesktop.features.images.ui.view.ImagesScreen
import io.github.openflocon.flocondesktop.features.network.ui.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.view.SharedPreferencesScreen
import io.github.openflocon.flocondesktop.features.table.ui.view.TableScreen
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.SubScreen
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.settings.SettingsScreen
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

    Box(modifier = modifier) {
        MainScreen(
            subScreen = subScreen,
            modifier = Modifier.fillMaxSize(),
            devicesState = devicesState,
            onDeviceSelected = viewModel::onDeviceSelected,
            leftPanelState = leftPanelState,
            onClickLeftPanelItem = viewModel::onClickLeftPanelItem,
        )
    }
}

@Composable
private fun MainScreen(
    subScreen: SubScreen,
    devicesState: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    leftPanelState: LeftPanelState,
    onClickLeftPanelItem: (LeftPanelItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(true) }
    val width by animateDpAsState(targetValue = if (expanded) PanelMaxWidth else PanelMinWidth)
    var windowSize by remember { mutableStateOf(IntSize.Zero) }
    val position by animateDpAsState(
        targetValue = if (expanded) PanelMaxWidth else PanelMinWidth
    )
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Row(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                windowSize = it.size // TODO Add windowsize lib
            }
    ) {
        LeftPanelView(
            modifier = Modifier
                .width(width)
                .fillMaxHeight(),
            expanded = expanded,
            onClickItem = onClickLeftPanelItem,
            state = leftPanelState,
            devicesState = devicesState,
            onDeviceSelected = onDeviceSelected
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(6.dp)
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
                        modifier =
                            Modifier
                                .fillMaxSize(),
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
            .background(FloconColors.pannel) // TODO Change
            .clickable(onClick = { expanded = !expanded })
    ) {
        FloconIcon(
            imageVector = Icons.Outlined.ChevronRight,
            tint = Color.LightGray,
            modifier = Modifier.rotate(rotate)
        )
    }
}

package io.github.openflocon.flocondesktop.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    Column(modifier) {
        // TODO navigation
        Row(modifier = Modifier.fillMaxSize()) {
            LeftPanelView(
                modifier = Modifier.width(300.dp)
                    .fillMaxHeight(),
                onClickItem = onClickLeftPanelItem,
                state = leftPanelState,
                devicesState = devicesState,
                onDeviceSelected = onDeviceSelected,
            )

            Box(modifier = Modifier.fillMaxSize().shadow(6.dp)) {
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
    }
}

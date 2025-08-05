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
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
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
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.nav.MainNavigator
import io.github.openflocon.flocondesktop.main.ui.nav.model.MainNavigation
import io.github.openflocon.flocondesktop.main.ui.settings.SettingsScreen
import io.github.openflocon.flocondesktop.main.ui.view.leftpannel.LeftPanelView
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = koinViewModel()
    val navigator: MainNavigator = koinInject()
    val backStack = navigator.backStack

    val leftPanelState by viewModel.leftPanelState.collectAsStateWithLifecycle()
    val devicesState by viewModel.devicesState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        MainScreen(
            backStack = backStack,
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
    backStack: List<MainNavigation>,
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
                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        // TODO
                    },
                    entryProvider = { key ->
                        when (key) {
                            MainNavigation.Analytics -> NavEntry(key) {
                                AnalyticsScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxSize(),
                                )
                            }

                            MainNavigation.Dashboard -> NavEntry(key) {
                                DashboardScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Database -> NavEntry(key) {
                                DatabaseScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Deeplinks -> NavEntry(key) {
                                DeeplinkScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxSize(),
                                )
                            }

                            MainNavigation.Files -> NavEntry(key) {
                                FilesScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Images -> NavEntry(key) {
                                ImagesScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Network -> NavEntry(key) {
                                NetworkScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Settings -> NavEntry(key) {
                                SettingsScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.SharedPreferences -> NavEntry(key) {
                                SharedPreferencesScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }

                            MainNavigation.Tables -> NavEntry(key) {
                                TableScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

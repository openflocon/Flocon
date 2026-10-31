package io.github.openflocon.flocondesktop.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.app.AboveDialogsZIndex
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.floconRoutes
import io.github.openflocon.flocondesktop.app.floconSceneStrategies
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.view.leftpannel.LeftPanel
import io.github.openflocon.flocondesktop.common.ui.featuredoc.LocalHasDevice
import io.github.openflocon.flocondesktop.common.ui.feedback.FeedbackHost
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.floconIsland
import io.github.openflocon.navigation.FloconNavigation
import io.github.openflocon.navigation.MainFloconNavigationState
import org.koin.compose.viewmodel.koinViewModel

/** All the plugins of a device, in a window of its own. Must be called in its [DeviceWindowScope]. */
@Composable
internal fun DeviceWindowScreen(
    deviceName: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<DeviceWindowViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DeviceWindowScreen(
        deviceName = deviceName,
        uiState = uiState,
        navigationState = viewModel.navigationState,
        onSelectMenu = viewModel::onSelectMenu,
        onTogglePin = viewModel::onTogglePin,
        onToggleSection = viewModel::onToggleSection,
        modifier = modifier,
    )
}

@Composable
private fun DeviceWindowScreen(
    deviceName: String,
    uiState: DeviceWindowUiState,
    navigationState: MainFloconNavigationState,
    onSelectMenu: (SubScreen) -> Unit,
    onTogglePin: (SubScreen) -> Unit,
    onToggleSection: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sceneStrategies = remember { floconSceneStrategies() }

    Box(modifier = modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalHasDevice provides uiState.isLive) {
            FloconNavigation(
                navigationState = navigationState,
                sceneStrategies = sceneStrategies,
                sceneDecoratorStrategies = listOf(
                    MenuSceneStrategy(
                        menuContent = { expanded, onToggleExpanded ->
                            LeftPanel(
                                current = uiState.contentState.current,
                                state = uiState.menuState,
                                expanded = expanded,
                                onToggleExpanded = onToggleExpanded,
                                onClickItem = { onSelectMenu(it.screen) },
                                onTogglePin = { onTogglePin(it.screen) },
                                onToggleSection = { onToggleSection(it.id) },
                            )
                        },
                        topBarContent = { DeviceWindowTopBar(deviceName = deviceName) },
                    ),
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                floconRoutes()
            }
        }
        FeedbackHost(modifier = Modifier.zIndex(AboveDialogsZIndex))
    }
}

/** The device of the window: it can not be changed, a window is opened for a device. */
@Composable
private fun DeviceWindowTopBar(
    deviceName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .floconIsland(
                Brush.horizontalGradient(
                    0.4f to FloconTheme.colors.islandTopBar,
                    1f to FloconTheme.colors.islandTopBarTint,
                ),
            )
            .padding(
                vertical = FloconTheme.spacing.medium,
                horizontal = FloconTheme.spacing.large,
            ),
    ) {
        Text(
            text = deviceName,
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun DeviceWindowTopBarPreview() {
    FloconTheme {
        DeviceWindowTopBar(deviceName = "Pixel 8 Pro")
    }
}

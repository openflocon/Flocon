package io.github.openflocon.flocondesktop.window

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.usecase.ObserveIsCurrentDeviceIAndAppIsActiveUseCase
import io.github.openflocon.flocondesktop.app.ContentUiState
import io.github.openflocon.flocondesktop.app.MenuNavigator
import io.github.openflocon.flocondesktop.app.ui.delegates.MenuDelegate
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.common.utils.stateInWhileSubscribed
import io.github.openflocon.navigation.MainFloconNavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
internal data class DeviceWindowUiState(
    val contentState: ContentUiState,
    val menuState: MenuState,
    // the device of the window is connected right now
    val isLive: Boolean,
)

/**
 * The left menu and the navigation of a window opened for a device.
 * Everything is resolved from the scope of the window, see [DeviceWindowScope]: its device, its navigation, its menu requests.
 */
internal class DeviceWindowViewModel(
    val navigationState: MainFloconNavigationState,
    private val menuDelegate: MenuDelegate,
    menuNavigator: MenuNavigator,
    observeIsCurrentDeviceAndAppActiveUseCase: ObserveIsCurrentDeviceIAndAppIsActiveUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val contentState = MutableStateFlow(ContentUiState(current = SubScreen.Timeline))

    private val menuState: StateFlow<MenuState> = menuDelegate.menuState
        .stateInWhileSubscribed(menuDelegate.initialMenuState())

    val uiState: StateFlow<DeviceWindowUiState> = combine(
        contentState,
        menuState,
        observeIsCurrentDeviceAndAppActiveUseCase(),
    ) { content, menu, isLive ->
        DeviceWindowUiState(
            contentState = content,
            menuState = menu,
            isLive = isLive,
        )
    }
        .stateInWhileSubscribed(
            DeviceWindowUiState(
                contentState = contentState.value,
                menuState = menuState.value,
                isLive = true,
            ),
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            // a feature can ask to display another screen of the menu (ex: from the timeline)
            menuNavigator.requests.collect { screen -> onSelectMenu(screen) }
        }

        viewModelScope.launch(dispatcherProvider.viewModel) {
            // the selected item can be hidden when another app is selected: go to the first available one
            menuState.collect { menu ->
                val available = menu.topItems + menu.pinnedItems + menu.sections.flatMap { it.items } + menu.bottomItems
                val current = contentState.value.current
                if (available.isNotEmpty() && available.none { it.screen == current }) {
                    onSelectMenu(available.first().screen)
                }
            }
        }
    }

    fun onSelectMenu(screen: SubScreen) {
        if (screen != SubScreen.Settings) {
            contentState.update { it.copy(current = screen) }
        }
        menuDelegate.navigateTo(screen)
    }

    fun onTogglePin(screen: SubScreen) {
        viewModelScope.launch(dispatcherProvider.viewModel) { menuDelegate.togglePin(screen) }
    }

    fun onToggleSection(sectionId: String) {
        viewModelScope.launch(dispatcherProvider.viewModel) { menuDelegate.toggleSection(sectionId) }
    }
}

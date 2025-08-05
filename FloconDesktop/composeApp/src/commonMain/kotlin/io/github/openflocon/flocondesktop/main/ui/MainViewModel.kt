package io.github.openflocon.flocondesktop.main.ui

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.flocondesktop.app.InitialSetupStateHolder
import io.github.openflocon.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import io.github.openflocon.flocondesktop.main.ui.delegates.DevicesDelegate
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelItem
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.main.ui.model.leftpanel.LeftPannelSection
import io.github.openflocon.flocondesktop.main.ui.nav.MainNavigator
import io.github.openflocon.flocondesktop.main.ui.nav.model.MainNavigation
import io.github.openflocon.flocondesktop.main.ui.view.displayName
import io.github.openflocon.flocondesktop.main.ui.view.icon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val devicesDelegate: DevicesDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val initialSetupStateHolder: InitialSetupStateHolder,
    private val mainNavigator: MainNavigator,
) : ViewModel(
    devicesDelegate,
) {
    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            initialSetupStateHolder.needsAdbSetup.collect {
                if (it) {
                    mainNavigator.setCurrentPage(MainNavigation.Settings)
                }
            }
        }
    }

    val leftPanelState = snapshotFlow { mainNavigator.backStack.firstOrNull() }.map { mainNavigation ->
        buildLeftPanelState(
            selectedId = mainNavigation?.id ?: MainNavigation.Network.id,
        )
    }.flowOn(dispatcherProvider.ui)
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = buildLeftPanelState(mainNavigator.backStack.firstOrNull()?.id ?: MainNavigation.Network.id),
        )

    val devicesState: StateFlow<DevicesStateUiModel> = devicesDelegate.devicesState

    fun onDeviceSelected(device: DeviceItemUiModel) {
        viewModelScope.launch {
            devicesDelegate.select(device.id)
        }
    }

    fun onClickLeftPanelItem(leftPanelItem: LeftPanelItem) {
        mainNavigationFromId(leftPanelItem.id)?.let {
            mainNavigator.setCurrentPage(it)
        }
    }
}

fun buildLeftPanelState(selectedId: String?) = LeftPanelState(
    bottomItems = listOf(
        item(MainNavigation = MainNavigation.Settings, selectedId = selectedId),
    ),
    sections = listOf(
        LeftPannelSection(
            title = "Network",
            items = listOf(
                item(MainNavigation = MainNavigation.Network, selectedId = selectedId),
                item(MainNavigation = MainNavigation.Images, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Storage",
            items = listOf(
                item(MainNavigation.Database, selectedId = selectedId),
                item(MainNavigation.SharedPreferences, selectedId = selectedId),
                item(MainNavigation.Files, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Data",
            items = listOf(
                item(MainNavigation.Dashboard, selectedId = selectedId),
                item(MainNavigation.Analytics, selectedId = selectedId),
                item(MainNavigation.Tables, selectedId = selectedId),
            ),
        ),
        LeftPannelSection(
            title = "Actions",
            items = listOf(
                item(MainNavigation.Deeplinks, selectedId = selectedId),
            ),
        ),
    ),
)

private fun item(
    MainNavigation: MainNavigation,
    selectedId: String?,
): LeftPanelItem {
    val id = MainNavigation.id
    return LeftPanelItem(
        id = id,
        icon = MainNavigation.icon(),
        text = MainNavigation.displayName(),
        isSelected = selectedId == id,
    )
}

val MainNavigation.id : String
    get() = when(this) {
        MainNavigation.Analytics -> "Analytics"
        MainNavigation.Dashboard -> "Dashboard"
        MainNavigation.Database -> "Database"
        MainNavigation.Deeplinks -> "Deeplinks"
        MainNavigation.Files -> "Files"
        MainNavigation.Images -> "Images"
        MainNavigation.Network -> "Network"
        MainNavigation.Settings -> "Settings"
        MainNavigation.SharedPreferences -> "SharedPreferences"
        MainNavigation.Tables -> "Tables"
    }

fun mainNavigationFromId(id: String) : MainNavigation? {
    return when(id) {
        "Analytics" -> MainNavigation.Analytics
        "Dashboard" -> MainNavigation.Dashboard
        "Database" -> MainNavigation.Database
        "Deeplinks" -> MainNavigation.Deeplinks
        "Files" -> MainNavigation.Files
        "Images" -> MainNavigation.Images
        "Network" -> MainNavigation.Network
        "Settings" -> MainNavigation.Settings
        "SharedPreferences" -> MainNavigation.SharedPreferences
        "Tables" -> MainNavigation.Tables
        else -> null
    }
}

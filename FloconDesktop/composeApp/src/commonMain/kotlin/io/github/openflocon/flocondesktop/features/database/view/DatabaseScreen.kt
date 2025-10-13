package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.DatabaseViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.flocondesktop.features.database.model.selectedDatabase
import io.github.openflocon.flocondesktop.features.database.view.databases_tables.DatabasesAndTablesView
import io.github.openflocon.library.designsystem.components.FloconFeature
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatabaseScreen(modifier: Modifier = Modifier) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val deviceDataBases by viewModel.deviceDataBases.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    DatabaseScreen(
        deviceDataBases = deviceDataBases,
        favorites = favorites,
        onAction = viewModel::onAction,
        tabs = tabs,
        selectedTab = selectedTab,
        modifier = modifier,
    )
}


@Composable
fun DatabaseScreen(
    deviceDataBases: DatabasesStateUiModel,
    favorites: List<DatabaseFavoriteQueryUiModel>,
    tabs: List<DatabaseTabState>,
    selectedTab: DatabaseTabState?,
    onAction: (DatabaseScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        Row(Modifier.fillMaxSize()) {
            DatabasesAndTablesView(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(340.dp),
                state = deviceDataBases,
                favorites = favorites,
                onAction = onAction
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                DatabaseTabsView(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    tabs = tabs,
                    selected = selectedTab,
                    onTabSelected = {
                        onAction(DatabaseScreenAction.OnTabSelected(it))
                    },
                    onCloseClicked = {
                        onAction(DatabaseScreenAction.OnTabCloseClicked(it))
                    },
                )
                selectedTab?.let {
                    DatabaseTabView(
                        tab = it,
                        favoritesTitles = remember(favorites) {
                            favorites.map { it.title }.toSet()
                        },
                    )
                }
            }
        }
    }

}

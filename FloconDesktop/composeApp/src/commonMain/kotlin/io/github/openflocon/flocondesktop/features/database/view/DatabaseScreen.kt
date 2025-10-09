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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.DatabaseViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.flocondesktop.features.database.model.selectedDatabase
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
        onDatabaseSelected = viewModel::onDatabaseSelected,
        onDatabaseDoubleClicked = viewModel::onDatabaseDoubleClicked,
        onTableDoubleClicked = viewModel::onTableDoubleClicked,
        onTabSelected = viewModel::onTabSelected,
        onTabCloseClicked = viewModel::onTabCloseClicked,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        deleteFavorite = viewModel::deleteFavorite,
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
    onDatabaseSelected: (DeviceDataBaseId) -> Unit,
    onDatabaseDoubleClicked: (DeviceDataBaseUiModel) -> Unit,
    onTableDoubleClicked: (DeviceDataBaseId, TableUiModel) -> Unit,
    onTabSelected: (DatabaseTabState) -> Unit,
    onTabCloseClicked: (DatabaseTabState) -> Unit,
    onFavoriteClicked: (DatabaseFavoriteQueryUiModel) -> Unit,
    deleteFavorite: (DatabaseFavoriteQueryUiModel) -> Unit,
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
                onDatabaseSelected = onDatabaseSelected,
                onDatabaseDoubleClicked = onDatabaseDoubleClicked,
                onFavoriteClicked = onFavoriteClicked,
                deleteFavorite = deleteFavorite,
                onTableDoubleClicked = { table ->
                    deviceDataBases.selectedDatabase()?.let {
                        onTableDoubleClicked(it.id, table)
                    }
                },
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                DatabaseTabsView(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    tabs = tabs,
                    selected = selectedTab,
                    onTabSelected = onTabSelected,
                    onCloseClicked = onTabCloseClicked,
                )
                selectedTab?.let {
                    DatabaseTabView(
                        tab = it,
                    )
                }
            }
        }
    }

}

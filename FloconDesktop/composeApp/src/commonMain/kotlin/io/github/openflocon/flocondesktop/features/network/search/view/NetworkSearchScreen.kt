package io.github.openflocon.flocondesktop.features.network.search.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkItemView
import io.github.openflocon.flocondesktop.features.network.search.NetworkSearchViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NetworkSearchScreen(
    onClose: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel = koinViewModel<NetworkSearchViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FloconTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChanged,
            placeholder = {
                Text("Search...")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SearchScope.entries.forEach { scope ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.onScopeToggled(scope) }
                ) {
                    FloconCheckbox(
                        checked = uiState.selectedScopes.contains(scope),
                        onCheckedChange = { viewModel.onScopeToggled(scope) }
                    )
                    Text(
                        text = scope.name,
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colorPalette.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(uiState.results) { item ->
                NetworkItemView(
                    state = item,
                    selected = false,
                    multiSelect = false,
                    multiSelected = false,
                    onAction = { action ->
                        if (action is NetworkAction.SelectRequest) {
                            onNavigateToDetail(action.id)
                        } else if (action is NetworkAction.DoubleClicked) {
                            onNavigateToDetail(action.item.uuid)
                        }
                    }
                )
            }
        }
    }
}

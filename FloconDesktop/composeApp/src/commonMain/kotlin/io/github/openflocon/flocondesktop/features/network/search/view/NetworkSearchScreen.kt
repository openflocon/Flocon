package io.github.openflocon.flocondesktop.features.network.search.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.models.text
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkItemView
import io.github.openflocon.flocondesktop.features.network.search.NetworkSearchUiState
import io.github.openflocon.flocondesktop.features.network.search.NetworkSearchViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NetworkSearchScreen(
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel = koinViewModel<NetworkSearchViewModel>()
    val query by viewModel.query
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkSearchScreen(
        query = query,
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onNavigateToDetail = onNavigateToDetail,
        onScopeToggled = viewModel::onScopeToggled,
    )
}

@Composable
fun NetworkSearchScreen(
    query: String,
    onQueryChanged: (String) -> Unit,
    onScopeToggled: (SearchScope) -> Unit,
    uiState: NetworkSearchUiState,
    onNavigateToDetail: (String) -> Unit,
) {
    FloconSurface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FloconTheme.colorPalette.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                FloconTextFieldWithoutM3(
                    value = query,
                    onValueChange = {
                        onQueryChanged(it)
                    },
                    placeholder = defaultPlaceHolder("Search..."),
                    leadingComponent = {
                        FloconIcon(
                            imageVector = Icons.Outlined.Search,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    containerColor = FloconTheme.colorPalette.secondary,
                    textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSurface),
                    modifier = Modifier.fillMaxWidth()
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    SearchScope.entries.forEach { scope ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onScopeToggled(scope) }
                        ) {
                            FloconCheckbox(
                                checked = uiState.selectedScopes.contains(scope),
                                onCheckedChange = { onScopeToggled(scope) }
                            )
                            Text(
                                text = scope.text(),
                                style = FloconTheme.typography.bodyMedium,
                                color = FloconTheme.colorPalette.onSurface
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(all = 16.dp),
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
}

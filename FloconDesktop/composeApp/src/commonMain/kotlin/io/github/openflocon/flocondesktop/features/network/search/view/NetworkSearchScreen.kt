package io.github.openflocon.flocondesktop.features.network.search.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkItemView
import io.github.openflocon.flocondesktop.features.network.search.Match
import io.github.openflocon.flocondesktop.features.network.search.NetworkSearchViewModel
import io.github.openflocon.flocondesktop.features.network.search.model.NetworkSearchUiState
import io.github.openflocon.flocondesktop.features.network.search.view.components.NetworkSearchPreviewView
import io.github.openflocon.flocondesktop.features.network.search.view.components.ScopeChipsView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NetworkSearchScreen() {
    val viewModel = koinViewModel<NetworkSearchViewModel>()
    val query by viewModel.query
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedRequestId by viewModel.selectedRequestId.collectAsStateWithLifecycle()
    val selectedRequest by viewModel.selectedRequest.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val currentMatchIndex by viewModel.currentMatchIndex.collectAsStateWithLifecycle()

    NetworkSearchScreen(
        query = query,
        uiState = uiState,
        selectedRequestId = selectedRequestId,
        selectedRequest = selectedRequest,
        matches = matches,
        currentMatchIndex = currentMatchIndex,
        onQueryChanged = viewModel::onQueryChanged,
        onNavigateToDetail = viewModel::onNavigateToDetail,
        onScopeToggled = viewModel::onScopeToggled,
        onSelectRequest = viewModel::onSelectRequest,
        onClosePreview = viewModel::onClosePreview,
        onNextMatch = viewModel::onNextMatch,
        onPrevMatch = viewModel::onPrevMatch,
    )
}

@Composable
private fun NetworkSearchScreen(
    query: String,
    onQueryChanged: (String) -> Unit,
    onScopeToggled: (SearchScope) -> Unit,
    uiState: NetworkSearchUiState,
    selectedRequestId: String?,
    selectedRequest: FloconNetworkCallDomainModel?,
    matches: List<Match>,
    currentMatchIndex: Int,
    onNextMatch: () -> Unit,
    onPrevMatch: () -> Unit,
    onSelectRequest: (String) -> Unit,
    onClosePreview: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

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
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    SearchScope.entries.forEach { scope ->
                        ScopeChipsView(
                            uiState = uiState,
                            scope = scope,
                            onScopeToggled = onScopeToggled
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    val listState = rememberLazyListState()
                    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.matchParentSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        items(uiState.results) { item ->
                            NetworkItemView(
                                state = item,
                                selected = item.uuid == selectedRequestId,
                                multiSelect = false,
                                multiSelected = false,
                                onAction = { action ->
                                    if (action is NetworkAction.SelectRequest) {
                                        onSelectRequest(action.id)
                                    } else if (action is NetworkAction.DoubleClicked) {
                                        onNavigateToDetail(action.item.uuid)
                                    }
                                }
                            )
                        }
                    }
                    FloconVerticalScrollbar(
                        adapter = scrollAdapter,
                        modifier = Modifier.fillMaxHeight()
                            .align(Alignment.TopEnd)
                    )
                }

                if (selectedRequestId != null && selectedRequest != null) {
                    NetworkSearchPreviewView(
                        request = selectedRequest,
                        matches = matches,
                        currentMatchIndex = currentMatchIndex,
                        onNextMatch = onNextMatch,
                        onPrevMatch = onPrevMatch,
                        onClose = onClosePreview,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                }
            }
        }
    }
}
